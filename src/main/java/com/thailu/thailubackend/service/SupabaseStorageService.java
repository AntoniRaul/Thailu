package com.thailu.thailubackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Sube un archivo a Supabase Storage y retorna la URL pública
     * @param file Archivo MultipartFile
     * @param folder Carpeta dentro del bucket (ej: "catalogo", "servicios")
     * @return URL pública de la imagen
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String path = folder + "/" + fileName;

            String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + path;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);
            headers.setContentType(MediaType.parseMediaType(file.getContentType()));

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                // URL pública para bucket público
                return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + path;
            } else {
                throw new RuntimeException("Error al subir archivo a Supabase: " + response.getBody());
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo", e);
        }
    }

    /**
     * Elimina un archivo de Supabase Storage dada su URL pública
     */
    public void deleteFile(String publicUrl) {
        try {
            String prefix = supabaseUrl + "/storage/v1/object/public/" + bucketName + "/";
            if (!publicUrl.startsWith(prefix)) return;

            String path = publicUrl.substring(prefix.length());
            String deleteUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + path;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, requestEntity, String.class);
        } catch (Exception e) {
            // Log error pero no detener el flujo
            System.err.println("Error al eliminar archivo de Supabase: " + e.getMessage());
        }
    }
}