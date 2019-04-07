package tech.iooo.coco.configuration;

import com.google.common.collect.Maps;
import java.util.Map;
import me.chanjar.weixin.common.api.WxConsts.EventType;
import me.chanjar.weixin.common.api.WxConsts.MenuButtonType;
import me.chanjar.weixin.common.api.WxConsts.XmlMsgType;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.constant.WxMpEventConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.iooo.coco.handler.KfSessionHandler;
import tech.iooo.coco.handler.LocationHandler;
import tech.iooo.coco.handler.LogHandler;
import tech.iooo.coco.handler.MenuHandler;
import tech.iooo.coco.handler.MsgHandler;
import tech.iooo.coco.handler.NullHandler;
import tech.iooo.coco.handler.ScanHandler;
import tech.iooo.coco.handler.StoreCheckNotifyHandler;
import tech.iooo.coco.handler.SubscribeHandler;
import tech.iooo.coco.handler.UnsubscribeHandler;

/**
 * Created on 2019-04-06 23:39
 *
 * @author <a href="mailto:yangkizhang@gmail.com?subject=iooo-wx-booster">Ivan97</a>
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationConfiguration {

  private static Map<String, WxMpMessageRouter> routers = Maps.newHashMap();
  private static Map<String, WxMpService> mpServices = Maps.newHashMap();
  @Autowired
  private ApplicationProperties applicationProperties;
  private LogHandler logHandler;
  private NullHandler nullHandler;
  private KfSessionHandler kfSessionHandler;
  private StoreCheckNotifyHandler storeCheckNotifyHandler;
  private LocationHandler locationHandler;
  private MenuHandler menuHandler;
  private MsgHandler msgHandler;
  private UnsubscribeHandler unsubscribeHandler;
  private SubscribeHandler subscribeHandler;
  private ScanHandler scanHandler;

  @Autowired
  public ApplicationConfiguration(LogHandler logHandler, NullHandler nullHandler, KfSessionHandler kfSessionHandler,
      StoreCheckNotifyHandler storeCheckNotifyHandler, LocationHandler locationHandler,
      MenuHandler menuHandler, MsgHandler msgHandler, UnsubscribeHandler unsubscribeHandler,
      SubscribeHandler subscribeHandler, ScanHandler scanHandler) {
    this.logHandler = logHandler;
    this.nullHandler = nullHandler;
    this.kfSessionHandler = kfSessionHandler;
    this.storeCheckNotifyHandler = storeCheckNotifyHandler;
    this.locationHandler = locationHandler;
    this.menuHandler = menuHandler;
    this.msgHandler = msgHandler;
    this.unsubscribeHandler = unsubscribeHandler;
    this.subscribeHandler = subscribeHandler;
    this.scanHandler = scanHandler;
  }

  @Bean
  public WxMpService wxMpService() {
    WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
    config.setAppId(applicationProperties.getWechat().getAppId());
    config.setSecret(applicationProperties.getWechat().getAppSecret());
    config.setToken(applicationProperties.getWechat().getToken());
    config.setAesKey(applicationProperties.getWechat().getAesKey());
    WxMpService wxService = new WxMpServiceImpl();
    wxService.setWxMpConfigStorage(config);
    return wxService;
  }

  @Bean
  public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
    final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

    // 记录所有事件的日志 （异步执行）
    newRouter.rule().handler(this.logHandler).next();

    // 接收客服会话管理事件
    newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
        .event(WxMpEventConstants.CustomerService.KF_CREATE_SESSION)
        .handler(this.kfSessionHandler).end();
    newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
        .event(WxMpEventConstants.CustomerService.KF_CLOSE_SESSION)
        .handler(this.kfSessionHandler)
        .end();
    newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
        .event(WxMpEventConstants.CustomerService.KF_SWITCH_SESSION)
        .handler(this.kfSessionHandler).end();

    // 门店审核事件
    newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
        .event(WxMpEventConstants.POI_CHECK_NOTIFY)
        .handler(this.storeCheckNotifyHandler).end();

    // 自定义菜单事件
    newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
        .event(MenuButtonType.CLICK).handler(this.menuHandler).end();

    // 点击菜单连接事件
    newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
        .event(MenuButtonType.VIEW).handler(this.nullHandler).end();

    // 关注事件
    newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
        .event(EventType.SUBSCRIBE).handler(this.subscribeHandler)
        .end();

    // 取消关注事件
    newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
        .event(EventType.UNSUBSCRIBE)
        .handler(this.unsubscribeHandler).end();

    // 上报地理位置事件
    newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
        .event(EventType.LOCATION).handler(this.locationHandler)
        .end();

    // 接收地理位置消息
    newRouter.rule().async(false).msgType(XmlMsgType.LOCATION)
        .handler(this.locationHandler).end();

    // 扫码事件
    newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
        .event(EventType.SCAN).handler(this.scanHandler).end();

    // 默认
    newRouter.rule().async(false).handler(this.msgHandler).end();
    return newRouter;
  }
}
