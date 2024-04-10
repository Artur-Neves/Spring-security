package com.mballem.curso.security.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.datatables.Datatables;
import com.mballem.curso.security.datatables.DatatablesColunas;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.repository.RepositoryUser;
import com.mballem.curso.security.web.Exceptions.AcessoNegadoException;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;


@Service
public class UsuarioService implements UserDetailsService{
	@Autowired
	private RepositoryUser repository;
	@Autowired
	private Datatables datatables;
	@Autowired
	private MedicoService medicoService;
	@Autowired
	private EmailService emailService;
	
	@Override
	@Transactional(readOnly= true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = findByEmailAndAtivo(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario "+ username +"não encontrado"));
		String [] a= null;
		if (usuario!=null|| !usuario.getPerfis().isEmpty()) {
		 a = new String[usuario.getPerfis().size()];
	    a = usuario.getPerfis().stream().map(p -> p.getAuthority()).toList().toArray(a);
		}
		return new User(
				usuario.getEmail(),
				usuario.getSenha(),
				AuthorityUtils.createAuthorityList(a)	
			);
	}
	
	private Optional<Usuario> findByEmailAndAtivo(String email) {
		return repository.findByEmailAndAtivoTrue(email);
	}

	public Usuario findByEmail(String email) {
		return repository.findByEmail(email);
	}
	@Transactional(readOnly= true)
	public Map<String, Object> buscarTodos(HttpServletRequest req) {
		datatables.setRequest(req);
		datatables.setColunas(DatatablesColunas.USUARIOS);
		String pesquisa = datatables.getSearch();
		Page<?> page = pesquisa.isEmpty() 
				? repository.findAll(datatables.getPageable())
						: repository.findAllByEmailContainingIgnoreCaseOrPerfisDescContainingIgnoreCase(pesquisa, pesquisa, datatables.getPageable()) ;
		return datatables.getResponse(page);
	}
	@Transactional
	public void salvarUsuario(Usuario usuario) {
		repository.save(usuario);
	}

	public Usuario findById(Long id) {
		return repository.getReferenceById(id);
	}

	public ModelAndView editarDeAcordoAoPerfil(List<Long> perfil, Usuario usuario) {
		if(perfil.contains(PerfilTipo.ADMIN.getCod()) &&
				   !perfil.contains(PerfilTipo.MEDICO.getCod())) {
					return  new  ModelAndView("usuario/cadastro", "usuario", usuario);
				}
				else if(perfil.contains(PerfilTipo.MEDICO.getCod())){
					return  new ModelAndView("medico/cadastro", "medico", medicoService.findByUsuarioId(usuario.getId()));
				}
				else if (perfil.contains(PerfilTipo.PACIENTE.getCod())){
					ModelAndView model = new ModelAndView("error");
					model.addAllObjects(PaginaErrorService.paginaDeErro("403", "Área restrita", "Os dados os dados do paciente são restritos."));
					return model;
				}
		return null;
	}
	@Transactional
	public String confirmarSenhaEditada(String s1, String s2, String s3,
			User user, RedirectAttributes attr) {
		String url = "redirect:/u/editar/senha";
		if (!s1.equals(s2)) {
			attr.addFlashAttribute("falha", "As senhas são diferentes");
			return url;
		}
		Usuario usuario = findByEmail(user.getUsername());
		if(!isSenhaCorreta(s3, usuario.getSenha())) {
			attr.addFlashAttribute("falha", "A senha atual não confere, tente novamente");
			return url;
		}
		editarSenha(usuario, s1);
		attr.addFlashAttribute("sucesso", "Senha alterada com sucesso.");
		return url;
		
	}

	public boolean isSenhaCorreta(String s3, String senha) {
		return new BCryptPasswordEncoder().matches(s3, senha);
	}
	public boolean compararSenhasCriptografadas(String tentativa, String senha) {
		 String hashSenha1 = tentativa.substring(0, 29); // A parte do hash contém 29 caracteres
		    String hashSenha2 = senha.substring(0, 29);

		    // Compara os hashes diretamente
		    return hashSenha1.equals(hashSenha2);
	}

	public void editarSenha(Usuario usuario, String s1) {
		usuario.setSenha(s1);
	}

	public void salvarCadastroPaciente(Usuario usuario) throws MessagingException {
		
			usuario.addPerfil(PerfilTipo.PACIENTE);
			repository.save(usuario);
			emailDeConfirmacaoDeCadastro(usuario.getEmail());			
		
	}
	
	public void emailDeConfirmacaoDeCadastro(String email) throws MessagingException {
		String codigo = Base64Utils.encodeToString(email.getBytes());
		emailService.enviarPedidoConfirmacaoDeCadastro(email, codigo);
	}
	@Transactional
	public void ativarCadastroPaciente(String codigo) {
		String email = new String( Base64Utils.decodeFromString(codigo));
		Usuario usuario = findByEmail(email);
		if(usuario.hasNotId()) {
			throw new AcessoNegadoException("Não foi possível ativar seu cadastro. Entre em"
					+ " contato com o suporte.");
		}
		usuario.setAtivo(true);
	}
	@Transactional
	public void pedidoRedefinirSenha(String email) throws MessagingException {
		Usuario usuario = findByEmailAndAtivo(email)
				.orElseThrow(() -> new AcessoNegadoException("Este email não existe no banco de dados, ou não esta ativo"));
		 String randomString = RandomStringUtils.randomNumeric(6);
		 usuario.setCodigoVerificador(randomString);
		 emailService.enviarPedidoRedefinirSenha(email, randomString);
		 
	}

	

}
