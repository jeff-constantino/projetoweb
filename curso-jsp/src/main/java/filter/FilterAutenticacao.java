package filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import connection.SingleConnectionBanco;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;



@WebFilter(urlPatterns = {"/principal/*"})/*intercepta todas as requisições do projeto ou mapeamento*/
public class FilterAutenticacao implements Filter {

	private static Connection connection;
    
    public FilterAutenticacao() {
    }

	public void destroy() { /*encerra os processos quando  parar o servidor*/
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
	}


	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			
		
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		
		String usuarioLogado = (String) session.getAttribute("usuario");
		
		String urlParaAutenticar = req.getServletPath(); /*url q esta sendo acessada*/
		
		/*Validar se esta logado, senao redireciona a tela de login*/
		
		if (usuarioLogado == null && !urlParaAutenticar.equalsIgnoreCase("/principal/ServletLogin")) { /*nao esta logado*/
			
			RequestDispatcher redireciona = request.getRequestDispatcher("/index.jsp?url=" +urlParaAutenticar);
			request.setAttribute("msg", "Por favor realize o login!");
			redireciona.forward(request, response);
			return;/*para a execucao e redireciona ao login*/
					}else {
						
						chain.doFilter(request, response);
					}
		
		connection.commit();/*deu tudo certo, comita as alterações no BD*/
		
		} catch (Exception e) {
			e.printStackTrace();
			RequestDispatcher redireciona = request.getRequestDispatcher("erro.jsp");
			request.setAttribute("msg", e.getMessage());
			redireciona.forward(request, response);
			
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	/*inicia os processos ou recursos quando o servidor sobe o projeto (iniciar conexao com BD)*/
	public void init(FilterConfig fConfig) throws ServletException {
		
		connection = SingleConnectionBanco.getConnection();
		
		
		
		
		
		
		
		
	}

}
