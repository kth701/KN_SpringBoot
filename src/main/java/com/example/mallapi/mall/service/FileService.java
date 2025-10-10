package com.example.mallapi.mall.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;


@Service
@Log4j2
public class FileService {

    // 1. 파일 업로드 => 파일을 생성(file outputstream)
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData ) throws Exception{

        // 중복없는 난수 발생
        UUID uuid = UUID.randomUUID();

        // "/images/test/abc/img/abc.png"
        //  => xxxx.substring(0, 4); index=0,1,2,3
        // => xxxx.substring(4) => index=4,5,...

        // 구 브라우저 버전은 첨부파일 적용시 <경로+파일명.확장자 모두 가져오>는 경우 파일명.확장자만 추출해야함.
        // 대부분 브라우저는 첨부파일 적용시 경로를 제외한 <파일명.확장자>만 가져옴.

        // 1. 경로를 제외한 파일이름.확장자 추출"  "/images/test/abc/img/abc.png" => "abc.png"추출
        String fileName = originalFileName.substring(originalFileName.lastIndexOf("\\")+1);
        // 2. 파일이름만 추출 : "abc.png" => "abc"
        String fname = fileName.substring(0, fileName.lastIndexOf("."));
        // 3. 확장자만 추출 " "abc.png" => ".png"
        String fext = originalFileName.substring(fileName.lastIndexOf("."));


        // 4. 파일이름+"_"+난수+"확장자"
        //    :업로드 저장될 파일이름 (업로드 파일이름 중복 제거)
        String savedFileName = fname+"_"+uuid.toString()+fext;
        // 5. 업로드 되었을 경우에 사용되는 경로 + 파일이름
        String fileUploadFullUrl = uploadPath + "/"+savedFileName;

        // 업로드 완료 => c:/upload/파일이름_난수.확장자
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();

        //  파일이름+"_"+난수+"확장자"
        //  경로를 제외한 새로만들어진 파일이름.확장자 반환
        return savedFileName;
    }
    // 2. 업로드된 파일 삭제
    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath);
        if (deleteFile.exists()){
            deleteFile.delete();
            log.info("----- 파일 삭제 완료");
        }else {
            log.info("----- 파일 존재하지 않습니다.");
        }
    }
}
