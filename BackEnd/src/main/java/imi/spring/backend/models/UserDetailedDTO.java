package imi.spring.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailedDTO extends UserDTO{
    protected Long id;
    protected String image;

    public UserDetailedDTO(AppUser appUser){
        super(appUser.getUsername(),
                appUser.getEmail(),
                appUser.getPassword()
        );

        this.id = appUser.getId();
        this.image = new String(
                Base64.getEncoder().encode(
                        appUser.getImage()
                )
        );
    }
}