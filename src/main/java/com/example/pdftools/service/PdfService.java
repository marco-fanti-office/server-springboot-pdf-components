package com.example.pdftools.service;

import com.example.pdftools.dto.PageSwitch;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@Service
@Slf4j
public class PdfService {

    public List<BufferedImage> extractPagesThumbnails(String pathFile, Integer dpi, ImageType imageType) throws Exception {

        //set default settings
        if (dpi == null) dpi = 300;
        if (imageType == null) imageType = ImageType.RGB;

        List<BufferedImage> thumbnailsCollection = new ArrayList<>();

        try {
            //File pdfFile = ResourceUtils.getFile("classpath:intellij-idea-help.pdf");
            File pdfFile = ResourceUtils.getFile("classpath:git_tutorial.pdf");
            //PDDocument document = PDDocument.load(new File(pdfFilename));
            PDDocument document = PDDocument.load(pdfFile);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                //BufferedImage bim = pdfRenderer.renderImageWithDPI(page, dpi, imageType);
                BufferedImage bim = pdfRenderer.renderImage(page,0.4F,imageType);
                thumbnailsCollection.add(bim);
            }
            document.close();

            return thumbnailsCollection;

        } catch (Throwable th) {
            log.error("Exception occurred <"+th.getLocalizedMessage()+">");
            throw th;
        }
    }

    public BufferedImage extractPageImage(String pathFile, Integer dpi, ImageType imageType, Integer indexPage) throws Exception {

        //set default settings
        if (dpi == null) dpi = 300;
        if (imageType == null) imageType = ImageType.RGB;

        List<BufferedImage> thumbnailsCollection = new ArrayList<>();

        try {
            //File pdfFile = ResourceUtils.getFile("classpath:intellij-idea-help.pdf");
            File pdfFile = ResourceUtils.getFile("classpath:git_tutorial.pdf");
            //PDDocument document = PDDocument.load(new File(pdfFilename));
            PDDocument document = PDDocument.load(pdfFile);
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            BufferedImage bim = pdfRenderer.renderImageWithDPI(indexPage,dpi,imageType);

            document.close();

            return bim;

        } catch (Throwable th) {
            log.error("Exception occurred <"+th.getLocalizedMessage()+">");
            throw th;
        }
    }

    public ByteArrayOutputStream reorderPages(List<Integer> listOfPositions) throws Exception {
        try {
            File pdfFile = ResourceUtils.getFile("classpath:git_tutorial.pdf");

            for (Integer position : listOfPositions) {
                log.info("position page" + position);
            }

            PDDocument newDoc = new PDDocument();
            PDDocument oldDoc = PDDocument.load(pdfFile);

            for ( int curPageCnt = 0; curPageCnt < oldDoc.getNumberOfPages(); curPageCnt++ ) {
                newDoc.addPage( ( PDPage )oldDoc.getPage( listOfPositions.get(curPageCnt) ) );
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            newDoc.save(outputStream);

            return outputStream;

        } catch (Throwable th) {
            log.error("Exception occurred <"+th.getLocalizedMessage()+">");
            throw th;
        }
    }

    /**
     * @param listOfPageIndex list of index of pages. !!! Start from zero !!!
     * @return a couple of ByteArrayOutputStream containing two output files.
     * @throws Exception
     *
     * Split a document into two new documents removing pages from a reference document
     * First document are pages moved from reference document
     * Second document contains pages not removed from reference document
     */
    public ByteArrayOutputStream[] splitDocumentWithPages(List<Integer> listOfPageIndex) throws Exception {
        try {
            File pdfFile = ResourceUtils.getFile("classpath:git_tutorial.pdf");

            for (Integer position : listOfPageIndex) {
                log.info("position page received" + position);
            }

            PDDocument newDoc0 = new PDDocument();
            PDDocument newDoc1 = new PDDocument();
            PDDocument referenceDoc = PDDocument.load(pdfFile);

            for ( int curPageCnt = 0; curPageCnt < referenceDoc.getNumberOfPages(); curPageCnt++ ) {

                if (listOfPageIndex.contains(curPageCnt)) {
                    newDoc0.addPage( ( PDPage )referenceDoc.getPage( curPageCnt ) );
                } else {
                    newDoc1.addPage( ( PDPage )referenceDoc.getPage( curPageCnt ) );
                }


            }

            ByteArrayOutputStream outputStream0Doc = new ByteArrayOutputStream();
            ByteArrayOutputStream outputStream1Doc = new ByteArrayOutputStream();
            newDoc0.save(outputStream0Doc);
            newDoc1.save(outputStream1Doc);

            ByteArrayOutputStream[] retValue = new ByteArrayOutputStream[2];
            retValue[0] = outputStream0Doc;
            retValue[1] = outputStream1Doc;

            return retValue;

        } catch (Throwable th) {
            log.error("Exception occurred <"+th.getLocalizedMessage()+">");
            throw th;
        }
    }

}
