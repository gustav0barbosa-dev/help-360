package com.help.config;

import com.help.model.*;
import com.help.model.enums.PerfilUsuario;
import com.help.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final AreaRepository areaRepository;
    private final SistemaRepository sistemaRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (empresaRepository.count() > 0) {
            System.out.println("Banco já populado. Pulando inicialização.");
            return;
        }

        // 1. Empresas
        Empresa empresa1 = criarEmpresa("SSPREV");
        Empresa empresa2 = criarEmpresa("DTR");

        // 2. Áreas
        criarArea("Recursos Humanos", empresa1);
        criarArea("Tecnologia da Informação", empresa1);
        criarArea("Financeiro", empresa2);

        // 3. Sistemas
        Sistema sistema1 = criarSistema("SIGEPREV");
        Sistema sistema2 = criarSistema("Help360");

        // 4. Categorias
        criarCategoria("Cadastro", sistema1);
        criarCategoria("Extinção", sistema1);
        criarCategoria("Acessos", sistema1);
        criarCategoria("Incidente", sistema2);
        criarCategoria("Dúvida", sistema2);

        // 5. Usuários
        criarUsuario("Administrador", "admin@help360.com", "Admin@123", PerfilUsuario.ADMIN, empresa1);
        criarUsuario("Gustavo Barbosa", "atendente@help360.com", "Atendente@123", PerfilUsuario.ATENDENTE, empresa1);
        criarUsuario("Maria Solicitante", "solicitante@help360.com", "Solicitante@123", PerfilUsuario.SOLICITANTE, empresa1);

        System.out.println("Banco de dados populado com sucesso!");
        System.out.println("Admin: admin@help360.com / Admin@123");
        System.out.println("Atendente: atendente@help360.com / Atendente@123");
        System.out.println("Solicitante: solicitante@help360.com / Solicitante@123");
    }

    private Empresa criarEmpresa(String nome) {
        Empresa e = new Empresa();
        e.setNome(nome);
        e.setAtivo(true);
        return empresaRepository.save(e);
    }

    private void criarArea(String nome, Empresa empresa) {
        Area a = new Area();
        a.setNome(nome);
        a.setEmpresa(empresa);
        a.setAtivo(true);
        areaRepository.save(a);
    }

    private Sistema criarSistema(String nome) {
        Sistema s = new Sistema();
        s.setNome(nome);
        s.setAtivo(true);
        return sistemaRepository.save(s);
    }

    private void criarCategoria(String nome, Sistema sistema) {
        Categoria c = new Categoria();
        c.setNome(nome);
        c.setSistema(sistema);
        c.setAtivo(true);
        categoriaRepository.save(c);
    }

    private void criarUsuario(String nome, String email, String senha, PerfilUsuario perfil, Empresa empresa) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(passwordEncoder.encode(senha));
        u.setPerfil(perfil);
        u.setEmpresa(empresa);
        u.setAtivo(true);
        usuarioRepository.save(u);
    }
}