package com.nlizzard.horizonhub.controller;

import cn.hutool.core.util.IdUtil;
import com.nlizzard.horizonhub.annotation.GlobalInterceptor;
import com.nlizzard.horizonhub.annotation.VerifyParam;
import com.nlizzard.horizonhub.basecontroller.BaseController;
import com.nlizzard.horizonhub.constants.Constants;
import com.nlizzard.horizonhub.entity.config.WebConfig;
import com.nlizzard.horizonhub.entity.enums.ResponseCodeEnum;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.exception.BusinessException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);


    @Resource
    private WebConfig webConfig;

    /**
     * 图片上传
     *
     * @param file 图片文件
     * @return 图片访问路径
     */
    @RequestMapping("uploadImage")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO<Map<String, String>> uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }
        // 拿到文件名，后缀名
        String fileName = file.getOriginalFilename();
        String fileSuffixName = null;
        if (fileName != null) {
            fileSuffixName = fileName.substring(fileName.lastIndexOf("."));
        }
        // 判断是否是图片文件
        if (!ArrayUtils.contains(Constants.IMAGE_ALL_SUFFIX, fileSuffixName)) {
            throw new BusinessException("文件格式不支持");
        }
        // 保存文件
        String path = saveFile(file, fileSuffixName);
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("fileName", path);
        return getSuccessResponseVO(fileMap);
    }

    /**
     * 保存图片文件到服务器
     *
     * @param file           图片文件对象
     * @param fileSuffixName 图片文件后缀
     * @return 文件访问路径
     */
    private String saveFile(MultipartFile file, String fileSuffixName) {
        try {
            // 生成唯一图片名称
            String fileRealName = IdUtil.getSnowflakeNextIdStr() + fileSuffixName;
            // 先把图片存放到 temp 文件夹
            String folderPath = webConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + File.separator + Constants.FILE_FOLDER_TEMP;
            File folder = new File(folderPath);
            if (!folder.exists()) {
                if (!folder.mkdirs()) throw new BusinessException(ResponseCodeEnum.CODE_500);
            }
            File uploadFile = new File(folderPath + File.separator + fileRealName);
            // 保存文件
            file.transferTo(uploadFile);
            return Constants.FILE_FOLDER_TEMP + "/" + fileRealName;
        } catch (Exception e) {
            logger.error("上传文件失败", e);
            throw new BusinessException("上传文件失败");
        }
    }

    /**
     * 获取用户头像
     *
     * @param userId 用户 ID
     */
    @RequestMapping("/getAvatar/{userId}")
    @GlobalInterceptor(checkParams = true)
    public void getAvatar(HttpServletResponse response,
                          @PathVariable @VerifyParam(required = true) String userId) {
        // TODO 待优化，当前头像只允许 jpg 格式，后续可以支持 png 等其他格式，并且user_info表没有存储头像路径
        String avatarFolderName = Constants.FILE_FOLDER_FILE + File.separator + Constants.FILE_FOLDER_AVATAR;
        String avatarPath = webConfig.getProjectFolder() + avatarFolderName + File.separator + userId + ".jpg";
        File file = new File(avatarPath);
        String imageFolder = Constants.FILE_FOLDER_AVATAR;
        String imageName = userId + ".jpg";
        if (!file.exists()) {
            imageName = "default_avatar.jpg";
            if (!new File(webConfig.getProjectFolder() + avatarFolderName + File.separator + imageName).exists()) {
                printNoDefaultImage(response);
                return;
            }
        }
        readImage(response, imageFolder, imageName);
    }

    /**
     * 输出无默认图提示
     *
     */
    private void printNoDefaultImage(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());

        // 不用手动关闭流，try-with-resources 会自动关闭
        try (PrintWriter writer = response.getWriter()) {
            writer.print("请在头像目录下放置默认头像default_avatar.jpg");
        } catch (Exception e) {
            logger.error("输出无默认图失败", e);
        }
    }

    /**
     * 获取图片
     *
     * @param imageFolder 图片文件夹
     * @param imageName   图片名称
     */
    @RequestMapping("/getImage/{imageFolder}/{imageName}")
    public void getImage(HttpServletResponse response,
                         @PathVariable String imageFolder,
                         @PathVariable String imageName) {
        readImage(response, imageFolder, imageName);
    }

    /**
     * 读取图片并写入响应
     *
     * @param response    HttpServletResponse 响应对象
     * @param imageFolder 图片文件夹
     * @param imageName   图片名称
     */
    private void readImage(HttpServletResponse response, String imageFolder, String imageName) {
        ServletOutputStream sos = null;
        FileInputStream in = null;
        try {
            if (StringUtils.isBlank(imageFolder) || StringUtils.isBlank(imageName)) {
                return;
            }
            String imageSuffix = imageName.substring(imageName.lastIndexOf("."));
            String filePath = webConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + File.separator + Constants.FILE_FOLDER_IMAGE + File.separator + imageFolder + File.separator + imageName;
            if (Constants.FILE_FOLDER_TEMP.equals(imageFolder)) {
                filePath = webConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + File.separator + imageFolder + File.separator + imageName;
            } else if (Constants.FILE_FOLDER_AVATAR.equals(imageFolder)) {
                filePath = webConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + File.separator + imageFolder + File.separator + imageName;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            imageSuffix = imageSuffix.replace(".", "");
            // 头像文件夹下的图片不设置缓存，其他图片设置1天缓存
            if (!Constants.FILE_FOLDER_AVATAR.equals(imageFolder)) {
                response.setHeader("Cache-Control", "max-age=86400");
            }
            response.setContentType("image/" + imageSuffix);
            in = new FileInputStream(file);
            sos = response.getOutputStream();
            byte[] bytes = new byte[8192];
            int len = 0;
            while ((len = in.read(bytes)) != -1) {
                sos.write(bytes, 0, len);
            }
        } catch (Exception e) {
            logger.error("读取图片异常", e);
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                } catch (IOException e) {
                    logger.error("readImage 方法关闭 ServletOutputStream 流报错", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("readImage 方法关闭 FileInputStream 流报错", e);
                }
            }
        }
    }
}
