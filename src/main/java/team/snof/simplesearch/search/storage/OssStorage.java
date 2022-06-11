package team.snof.simplesearch.search.storage;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.Index;

import java.io.*;
import java.util.List;

/**
 * @author czt
 */
@Component
public class OssStorage {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 配置直接写这了
     */
    public static final String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
    public static final String accessKeyId = "LTAI5tBETJ2GohEyjhVxzvGv";
    public static final String accessKeySecret = "GA0DbmBFXTSudbCwh2wuZLMLqBuqBw";
    public static final String bucketName = "simple-search";

    private static final OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

    /**
     * 通过唯一id获取Doc
     * 若id不存在返回null，且控制台打印错误
     * @param id
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Doc getBySnowId(Long id){
        Query query = new Query(Criteria.where("_id").is(id));
        List<Doc> docs = mongoTemplate.find(query, Doc.class, "doc_storage");
        if (docs.isEmpty()) {
            return null;
        }

        return docs.get(0);


//        Doc doc = null;
//        try{
//            OSSObject ossObject = ossClient.getObject(bucketName, String.valueOf(id));
//            InputStream is = ossObject.getObjectContent();
//            ObjectInputStream ois = new ObjectInputStream(is);
//            doc = (Doc)ois.readObject();
//            ois.close();
//        } catch (OSSException oe) {
//            System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                    + "but was rejected with an error response for some reason.");
//            System.out.println("Error Message:" + oe.getErrorMessage());
//            System.out.println("Error Code:" + oe.getErrorCode());
//            System.out.println("Request ID:" + oe.getRequestId());
//            System.out.println("Host ID:" + oe.getHostId());
//        } catch (Throwable ce) {
//            System.out.println("Caught an ClientException, which means the client encountered "
//                    + "a serious internal problem while trying to communicate with OSS, "
//                    + "such as not being able to access the network.");
//            System.out.println("Error Message:" + ce.getMessage());
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
//        return doc;
    }

    /**
     * 上传Doc到阿里云oss（key：Doc.SnowflakeDocId，value：Doc）
     * @param doc
     * @throws IOException
     */
    public Doc addDoc(Doc doc) throws IOException {
        return mongoTemplate.save(doc, "doc_storage");

//        if(doc == null) {
//            return;
//        }
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//        try {
//            ByteArrayOutputStream bas = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(bas);
//            oos.writeObject(doc);
//            oos.flush();
//            oos.close();
//            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, String.valueOf(doc.getSnowflakeDocId()), new ByteArrayInputStream(bas.toByteArray()));
//            ossClient.putObject(putObjectRequest);
//        } catch (OSSException oe) {
//            System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                    + "but was rejected with an error response for some reason.");
//            System.out.println("Error Message:" + oe.getErrorMessage());
//            System.out.println("Error Code:" + oe.getErrorCode());
//            System.out.println("Request ID:" + oe.getRequestId());
//            System.out.println("Host ID:" + oe.getHostId());
//        } catch (ClientException ce) {
//            System.out.println("Caught an ClientException, which means the client encountered "
//                    + "a serious internal problem while trying to communicate with OSS, "
//                    + "such as not being able to access the network.");
//            System.out.println("Error Message:" + ce.getMessage());
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
    }
}
