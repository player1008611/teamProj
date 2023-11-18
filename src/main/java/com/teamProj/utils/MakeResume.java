package com.teamProj.utils;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class MakeResume {
    public static byte[] makeResume(Map<String, Object> map) throws IOException {
        File resumeTemplate = new File("src/main/java/com/teamProj/utils/Resume.pdf");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(resumeTemplate);
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(reader, writer);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, false);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey().equals("image")) {
                continue;
            }
            PdfFormField field = form.getField(entry.getKey());
            field.setValue(entry.getValue().toString());
        }
        if (map.get("image") != null) {
            Image image = (Image) map.get("image");
            PdfFormField field = form.getField("image");
            List<PdfWidgetAnnotation> widgets = field.getWidgets();
            PdfWidgetAnnotation widget = widgets.get(0);
            float x1 = widget.getRectangle().getAsNumber(0).floatValue();
            float y1 = widget.getRectangle().getAsNumber(1).floatValue();
            float x2 = widget.getRectangle().getAsNumber(2).floatValue();
            float y2 = widget.getRectangle().getAsNumber(3).floatValue();
            float fieldWidth = x2 - x1;
            float fieldHeight = y2 - y1;
            image.scaleToFit(fieldWidth, fieldHeight);
            float centerX = x1 + fieldWidth / 2 - image.getImageScaledWidth() / 2;
            float centerY = y1 + fieldHeight / 2 - image.getImageScaledHeight() / 2;
            image.setFixedPosition(centerX, centerY);
            Document document = new Document(pdfDocument);
            document.add(image);
            document.close();
        }
        //TODO 修复奇怪的exception
        //form.flattenFields();
        pdfDocument.close();
        return outputStream.toByteArray();
    }
}
