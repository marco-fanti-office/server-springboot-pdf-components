package com.example.pdftools.controller;

import com.example.pdftools.dto.PageNumber;
import com.example.pdftools.dto.PageSwitch;
import com.example.pdftools.dto.Thumbnail;
import com.example.pdftools.service.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.nio.ch.IOUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/pdf-services")
public class PdfServiceController {

    @Autowired
    PdfService pdfService;
    
    @GetMapping(path = "/create-thumbnails", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Thumbnail> createThumbnails() {
        log.info("serve /create-thumbnails");
        List<Thumbnail> retValue = new ArrayList<>();
        try {
            List<BufferedImage> bimCollection = pdfService.extractPagesThumbnails(null, 300, null);
            for (int i = 0; i < bimCollection.size(); i++) {
                BufferedImage bim = bimCollection.get(i);
                Thumbnail thumbnail = new Thumbnail();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                ImageIO.write(bim, "jpg", baos);

                //ImageIO.write(bim, "jpg", new File("/tmp/Thumb"+i+".jpg"));


                thumbnail.setIdPage(i);
                thumbnail.setBase64ByteArray(Base64.getEncoder().encodeToString(baos.toByteArray()));
                retValue.add(thumbnail);
                //log.info("Created thumbnail "+i);
                //log.info("Base64 "+thumbnail.getBase64ByteArray());
            }
        } catch (Exception e) {
            log.error("Exception serving /create-thumbnails",e);
            throw new RuntimeException(e);
        }
        return retValue;
    }

    @GetMapping(path = "/page-preview-base64", produces = MediaType.APPLICATION_JSON_VALUE)
    public Thumbnail getPagePreviewBase64(@RequestParam(name="pageIndex") Integer pageIndex) {
        log.info("serve /page-preview-base64");

        try {
            BufferedImage bim = pdfService.extractPageImage(null, 300, null, pageIndex);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bim, "jpg", baos);

            Thumbnail thumbnail = new Thumbnail();
            thumbnail.setBase64ByteArray(Base64.getEncoder().encodeToString(baos.toByteArray()));
            thumbnail.setIdPage(pageIndex);
            return thumbnail;

        } catch (Exception e) {
            log.error("Exception serving /create-thumbnails",e);
            throw new RuntimeException(e);
        }

    }

    @GetMapping(
            value = "/pdf-document",
            name = "git_tutorial.pdf",
            produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] getPdf() throws IOException {
        log.info("serve /pdf-documents");
        InputStream in = getClass()
                .getResourceAsStream("/git_tutorial.pdf");


        if(in != null) {
            byte[] bArray = IOUtils.toByteArray(in);
            log.info(" - " + bArray.length);
            return bArray;
        } else {
            log.info("resource null");
        }
        return null;
    }

    @PutMapping(
            value = "/reorder-pages",
            name = "git_tutorial_switched.pdf",
            produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] getPdfReordered(@RequestBody PageSwitch[] pageSwitchesArray) throws Exception {
        log.info("serve /reorder-pages");
        InputStream in = getClass()
                .getResourceAsStream("/git_tutorial.pdf");

        Integer newPositions[] = new Integer[pageSwitchesArray.length];

        for (PageSwitch change : pageSwitchesArray) {
            newPositions[change.getToIndex()]=change.getFromIndex();
        }

        ByteArrayOutputStream bAOS = pdfService.reorderPages(Arrays.asList(newPositions));

        if(bAOS != null) {

            FileOutputStream fos = new FileOutputStream(new File("/tmp/git_tutorial_switched.pdf"));
            bAOS.writeTo(fos);

            byte[] bArray = bAOS.toByteArray();
            log.info(" - " + bArray.length);
            fos.close();
            bAOS.close();
            return bArray;
        } else {
            log.info("resource null");
        }
        return null;
    }


    @PutMapping(
            value = "/split-document-for-pages")
    public void splitDocumentForPages(@RequestBody PageNumber[] pageNumbersArray) throws Exception {
        log.info("serve /split-document-for-pages");
        InputStream in = getClass()
                .getResourceAsStream("/git_tutorial.pdf");

        Integer indexIntegersArray[] = new Integer[pageNumbersArray.length];

        int i=0;
        for (PageNumber pNumber : pageNumbersArray) {
            indexIntegersArray[i]=pageNumbersArray[i].getIndex();
            i++;
        }

        ByteArrayOutputStream[] bAOS = pdfService.splitDocumentWithPages(Arrays.asList(indexIntegersArray));

        if(bAOS != null) {

            if(bAOS[0] != null) {
                FileOutputStream fos = new FileOutputStream(new File("/tmp/git_tutorial_0.pdf"));
                bAOS[0].writeTo(fos);

                byte[] bArray = bAOS[0].toByteArray();
                log.info("File 0 - " + bArray.length);
                fos.close();
                bAOS[0].close();
            }

            if(bAOS[1] != null) {
                FileOutputStream fos = new FileOutputStream(new File("/tmp/git_tutorial_1.pdf"));
                bAOS[1].writeTo(fos);

                byte[] bArray = bAOS[1].toByteArray();
                log.info("File 1 - " + bArray.length);
                fos.close();
                bAOS[1].close();
            }

        } else {
            log.info("resource null");
        }

    }

}
