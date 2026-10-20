package com.help.repository;

import com.help.model.Usuario;
import com.help.model.enums.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // RF0011 - Autenticação
    Optional<Usuario> findByEmail(String email);

    // RN0062 - E-mail único
    boolean existsByEmail(String email);

    // RN0064 - Listar apenas usuários ATENDENTES ativos para atribuir como responsável
    List<Usuario> findByPerfilAndAtivoTrue(PerfilUsuario perfil);

    // Para filtros de listagem de tickets
    List<Usuario> findByEmpresaIdAndAtivoTrue(Long empresaId);
}