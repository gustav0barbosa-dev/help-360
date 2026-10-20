package com.help.model;

import com.help.model.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.lang.Override;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



@Entity
@Table(name = "tb_usuario")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email; // RN0062 - e-mail único

    @Column(nullable = false)
    private String senha; // Criptografada com BCrypt (RNF0022)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfil; // RN0061

    @Column(nullable = false)
    private Boolean ativo = true;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @OneToMany(mappedBy = "solicitante")
    private List<Ticket> ticketsSolicitados = new ArrayList<>();

    @OneToMany(mappedBy = "responsavel")
    private List<Ticket> ticketsResponsaveis = new ArrayList<>();

    // ===== Métodos do UserDetails =====
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converte o Perfil em uma Authority (ROLE_ADMIN, ROLE_ATENDENTE, etc)
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return ativo; }


}