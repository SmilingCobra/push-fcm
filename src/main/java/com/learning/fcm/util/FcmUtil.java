package com.learning.fcm.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FcmUtil {


    private static Map<String, FirebaseApp> firebaseAppMap = new ConcurrentHashMap<>();



    /**
     *
     * @param host  ip地址
     * @param port  端口号
     * @param username  如果有则填
     * @param password  如果有则填
     */
    private static void initProxy(String host, int port, final String username,final String password) {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        //HTTP代理
        System.setProperty("http.proxyHost",host);
        System.setProperty("http.proxyPort",Integer.toString(port));
        //HTTPS代理
        System.setProperty("https.proxyHost",host);
        System.setProperty("https.proxyPort",Integer.toString(port));

        if(username != null && password !=null)
            Authenticator.setDefault(new BasicAuthenticator(username,password));
    }



    /**
     *
     * @param appId 需要开发的appId 这个id和发送消息没有任何关系，只是作为key为了查找app对应的FirebaseApp信息使用
     * @param title 你要推送的消息标题
     * @param body  消息内容
     * @param packageName  这个包名 是ios/android在fcm官网注册获取到的包名
     * @param clickAction  这个是点击消息后的触发事件 ，如果想要触发app可以默认设置为OPEN_STOCK_ACTIVITY
     * @param ttl   消息过期时间 单位在api要求为ms
     * @param channelId  设置发送的频道的id
     * @param tokens  用户设备唯一标识列表，其中包含若干个用户设备，官网限制为100个
     * @return
     * @throws IOException
     */
    public static BatchResponse push(String appId, String title, String body, String packageName, String clickAction, Long ttl, String channelId, List<String> tokens) throws IOException {
       BatchResponse batchResponse = null;

        if(!isInit(appId))
            initSDK(appId);
        FirebaseApp firebaseApp = Constants.FIREBASE_APP_MAP.get(appId);
        try {
            if(firebaseApp !=null){
                //获取AndroidConfig.Builder对象
                AndroidConfig.Builder androidConfigBuilder=AndroidConfig.builder();
                //获取AndroidNotification.Builder对象
                AndroidNotification.Builder androidNotifiBuilder=AndroidNotification.builder();
                //可以存放一个数据信息进行发送，使得app开发客户端可以接受信息
                androidConfigBuilder.putData("test","this is a test data");
                androidConfigBuilder.setRestrictedPackageName(packageName);//设置包名
                androidConfigBuilder.setTtl(ttl);//设置过期时间 官方文档以毫秒为单位
                androidNotifiBuilder.setTitle(title);// 设置消息标题
                androidNotifiBuilder.setBody(body);// 设置消息内容
                androidNotifiBuilder.setClickAction(clickAction); //设置触发事件
                androidNotifiBuilder.setChannelId(channelId);
                AndroidNotification androidNotification=androidNotifiBuilder.build();
                androidConfigBuilder.setNotification(androidNotification);

                AndroidConfig androidConfig=androidConfigBuilder.build();

                //在进行消息发送之前要设置代理  这个非常重要，因为访问谷歌的服务器需要通过代理服务器在进行访问
                initProxy("yourHost",80,"yourUsername","yourPassword");

                //构建消息
                MulticastMessage message = MulticastMessage.builder()
                        .addAllTokens(tokens) //向所有的设备推送消息
                        .setAndroidConfig(androidConfig)
                        .build();
                batchResponse = FirebaseMessaging.getInstance(firebaseApp).sendMulticast(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return batchResponse;

    }
    private static void initSDK(String appId) throws IOException {

        String jsonPath = ""; //存放加密信息的部分
        if(jsonPath!=null) {
            FileInputStream serviceAccount = new FileInputStream(jsonPath);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            //初始化firebaseApp
            FirebaseApp firebaseApp = null;
            try {
                firebaseApp = FirebaseApp.initializeApp(options);
            } catch (Exception e) {
                firebaseApp = FirebaseApp.getInstance(appId);
                if(firebaseApp!=null){
                    firebaseApp.delete();
                    firebaseApp = FirebaseApp.initializeApp(options);
                }
            }
            //存放
            Constants.FIREBASE_APP_MAP.put(appId, firebaseApp);
        }
    }
    private static boolean isInit(String appId) {
        return firebaseAppMap.containsKey(appId);
    }
}
