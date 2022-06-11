package team.snof.simplesearch.search.storage;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.internal.OSSUtils;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.io.*;

/**
 * @author czt
 */
public class OssStorage {

    /**
     * 配置直接写这了
     */
    public static final String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
    public static final String accessKeyId = "LTAI5tBETJ2GohEyjhVxzvGv";
    public static final String accessKeySecret = "GA0DbmBFXTSudbCwh2wuZLMLqBuqBw";
    public static final String bucketName = "simple-search";


    private volatile static OSS ossClient;

    /**
     * 获取单例Oss
     * @return
     * @throws OSSException
     */
    private static OSS getOssClient() throws OSSException {
        if (ossClient == null) {
            synchronized (OSSUtils.class) {
                if (ossClient == null) {
                    // 创建ClientConfiguration实例，按照您的需要修改默认参数。
//                    ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
                    // 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
//                    conf.setMaxConnections(200);
                    // 设置Socket层传输数据的超时时间，默认为50000毫秒。
//                    conf.setSocketTimeout(10000);
                    // 设置建立连接的超时时间，默认为50000毫秒。
//                    conf.setConnectionTimeout(10000);
                    // 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
//                    conf.setConnectionRequestTimeout(1000);
                    // 设置连接空闲超时时间。超时则关闭连接，默认为60000毫秒。
//                    conf.setIdleConnectionTime(10000);
                    // 设置失败请求重试次数，默认为3次。
//                    conf.setMaxErrorRetry(5);
                    // 设置是否支持将自定义域名作为Endpoint，默认支持。
//                    conf.setSupportCname(true);
                    // 设置是否开启二级域名的访问方式，默认不开启。
//                    conf.setSLDEnabled(true);
                    // 设置连接OSS所使用的协议（HTTP/HTTPS），默认为HTTP。
//                    conf.setProtocol(Protocol.HTTP);
                    // 设置用户代理，指HTTP的User-Agent头，默认为aliyun-sdk-java。
//                    conf.setUserAgent("aliyun-sdk-java");
                    // 设置代理服务器端口。
//                    conf.setProxyHost("<yourProxyHost>");
                    // 设置代理服务器验证的用户名。
//                    conf.setProxyUsername("<yourProxyUserName>");
                    // 设置代理服务器验证的密码。
//                    conf.setProxyPassword("<yourProxyPassword>");
                    try {
                        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
                    } catch (OSSException e) {
                        throw new OSSException(e.getMessage(), e);
                    }

                }
            }
        }
        return ossClient;
    }

    /**
     * 操作完之后调用 关闭连接
     */
    public static void stopOss() {
        if(ossClient != null){
            ossClient.shutdown();
//            ossClient设置为null  因为是单例的  一直会拿已关闭的连接去访问  会网络错误？
            ossClient = null;
        }
    }



    /**
     * 通过唯一id获取Doc
     * 若id不存在返回null，且控制台打印错误
     * @param id
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Doc getBySnowId(Long id){
        OSS ossClient = getOssClient();
        Doc doc = null;
        try{
            OSSObject ossObject = ossClient.getObject(bucketName, String.valueOf(id));
            InputStream is = ossObject.getObjectContent();
            ObjectInputStream ois = new ObjectInputStream(is);
            doc = (Doc)ois.readObject();
            ois.close();
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (Throwable ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
        }
        return doc;
    }

    /**
     * 上传Doc到阿里云oss（key：Doc.SnowflakeDocId，value：Doc）
     * @param doc
     * @throws IOException
     */
    public static void addDoc(Doc doc) throws Exception {
        if(doc == null) {
            return;
        }
        OSS ossClient = getOssClient();
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bas);
            oos.writeObject(doc);
            oos.flush();
            oos.close();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, String.valueOf(doc.getSnowflakeDocId()), new ByteArrayInputStream(bas.toByteArray()));
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
        }
    }
}
