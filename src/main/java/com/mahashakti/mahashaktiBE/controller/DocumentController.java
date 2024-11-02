package com.mahashakti.mahashaktiBE.controller;

import com.mahashakti.mahashaktiBE.service.DocumentService;
import com.mahashakti.mahashaktiBE.utils.Helper;
import com.mahashakti.mahashaktiBe.api.DocumentApi;
import com.mahashakti.mahashaktiBe.model.DocumentData;
import com.mahashakti.mahashaktiBe.model.MahashaktiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DocumentController implements DocumentApi {

    private final DocumentService documentService;

    @Override
    public ResponseEntity<Object> postGenerateDocument(DocumentData documentData) {
        byte[] documentByteDate = documentService.generateDocument(documentData.getName(), documentData.getDetails(),
                documentData.getHeaders(), documentData.getData(), documentData.getSummaryHeaders(), documentData.getSummaryData());

        HttpHeaders headersHttp = new HttpHeaders();
        headersHttp.setContentType(MediaType.APPLICATION_PDF);
        headersHttp.setContentDispositionFormData("attachment", "generated_report.pdf");

        return ResponseEntity.ok()
                .headers(headersHttp)
                .body(documentByteDate);
    }
}