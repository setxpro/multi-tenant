package br.com.innoact.multi_tanancy.infra.persistences.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    public Long userId;

    public String fullname;

    @Column(name = "resume_name")
    public String resumeName;

    public String email;

    @Column(name = "phone_number")
    public String phoneNumber;

}
