package tech.iooo.coco.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created on 2019-04-06 23:42
 *
 * @author <a href="mailto:yangkizhang@gmail.com?subject=iooo-wx-booster">Ivan97</a>
 */
@Data
@ConfigurationProperties(prefix = "application", ignoreInvalidFields = true)
public class ApplicationProperties {

  private ApplicationProperties.Wechat wechat = new ApplicationProperties.Wechat();

  @Data
  public static class Wechat {

    private String appId;
    private String appSecret;
    private String token;
    private String aesKey;
  }
}
