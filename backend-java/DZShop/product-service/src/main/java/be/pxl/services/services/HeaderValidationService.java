package be.pxl.services.services;

import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
@Data
public class HeaderValidationService {
    public String user;

    public boolean hasAdminRole(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null) {
            return false;
        }

        String decoded = new String(java.util.Base64.getDecoder().decode(authorizationHeader.replace("Basic ", "")));
        String[] credentials = decoded.split(":");

        user = credentials[0];

        // Let's assume the credentials are "admin:adminpass"
        return "admin".equals(credentials[0]); // Return true if the username is 'admin'
    }

    public String isLoggedIn(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null) {
            return null;
        }

        String decoded = new String(java.util.Base64.getDecoder().decode(authorizationHeader.replace("Basic ", "")));
        String[] credentials = decoded.split(":");

        user = credentials[0];

        return credentials[0];
    }

}
