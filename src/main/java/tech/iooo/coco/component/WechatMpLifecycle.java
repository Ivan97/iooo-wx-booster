package tech.iooo.coco.component;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * Created on 2019-04-06 23:38
 *
 * @author <a href="mailto:yangkizhang@gmail.com?subject=iooo-wx-booster">Ivan97</a>
 */
@Component
public class WechatMpLifecycle implements SmartLifecycle {

  @Override
  public void start() {
    
  }

  @Override
  public void stop() {

  }

  @Override
  public boolean isRunning() {
    return false;
  }
}
