package com.djl.shop.resource.util;

import com.djl.common.config.ProjectConfig;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * @author DJL
 * @create 2019-06-22 20:23
 * @desc 上传文件工具类
 **/
public class UploadFileUtil {

    // 文件上传token
    private String upToken;
    // 指定Zone对象的配置类
    private Configuration cfg;

    public UploadFileUtil() {
        // 生成token
        this.upToken = Auth.create(ProjectConfig.KODO_ACCESSKEY, ProjectConfig.KODO_SECRETKEY)
                .uploadToken(ProjectConfig.KODO_BUCKET);
        // 生成配置类
        this.cfg = new Configuration(Zone.zone0());
    }


    /**
     * 上传成功返回图片url地址
     * @param file
     * @return
     */
    public String uploadMultipartFile(MultipartFile file) {
        // 上传管理类
        UploadManager uploadManager = new UploadManager(this.cfg);

        // 生成随机文件名
        String filename = renameFile(file.getOriginalFilename());

        try {
            byte[] uploadBytes = file.getBytes();
            ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);

            try {
                Response response = uploadManager.put(byteInputStream,filename,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                // 上传成功返回图片地址
                if (filename.equals(putRet.key)) {
                    return createUrl(filename);
                } else {
                    throw new Exception("上传文件失败");
                }

            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (UnsupportedEncodingException ex) {
            //ignore
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 生成文件访问的url
    private String createUrl(String filename) throws UnsupportedEncodingException {
        String fileName = filename;
        String domainOfBucket = "http://pth8xvitk.bkt.clouddn.com";
        String encodedFileName = URLEncoder.encode(fileName, "utf-8");
        String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        String accessKey = ProjectConfig.KODO_ACCESSKEY;
        String secretKey = ProjectConfig.KODO_SECRETKEY;
        Auth auth = Auth.create(accessKey, secretKey);
        long expireInSeconds = 36000;//10小时，可以自定义链接过期时间
        String finalUrl = auth.privateDownloadUrl(publicUrl, expireInSeconds);

        return finalUrl;
    }

    // 重命名文件名称
    //截取并重命名
    private String renameFile(String fileName){
        if(fileName.length()>50){
            fileName=fileName.substring(fileName.length()-50);
        }
        return System.currentTimeMillis()+"_"+ UUID.randomUUID().toString().replace("-","")+"_"+fileName;
    }

}
