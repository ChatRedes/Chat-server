# Server
## Database
Para o servidor rodar é necessario mante-lo conectado a um banco de dados PostgreSQL. Para realizar a conexão alguns requisitos devem ser cumpridos:
* Ter JDBC instalado e configurado no projeto. Pode ser feito através do download do programa no link https://jdbc.postgresql.org/download/ na opção Java 8 e JDBC versão 42.7.3. O download trará um arquivo .jar que deve ser salvo em uma pasta onde o IntelliJ IDEA tenha acesso. Após download, clicke na pasta do projeto com o botão direito e procure a opção "Open Module Settings", procure a aba "dependencies" na opção " Modules", clicke no + e na opção "JARs or Directories" e selecione o .jar baixado. Clicke em OK salvando e fechando esta aba
* Ter um banco de dados PostgreSQL rodando em um local acessível ao servidor, seja este o prórpio servidor ou uma máquina cujo servidor consiga acessar via rede.
* Criar e modificar o arquivo .env. Um arquivo exemplo disponível no repositório deve ser utilizado como base para criação do .env. Nele deve conter as informações relativos ao banco de dados, tal qual seu HOST, porta onde ele está rodando, nome do banco de dados, nome de usuario e senha.
OBS: Para utilização da biblioteca Dotenv é necessário abrir a aba "dependencies" visitada ao instalar o JDBC, lá clickamos no + e a opção escolhida deve ser "Library..." "From Maven". Será necessário informar a biblioteca, neste caso io.github.cdimascio.dotenv.java, e escolher um local para download com pelo menos a opção "Transitive Dependencies" marcada.

As operações no banco de dados devem decorrer na seguinte ordem:
```java
public class Model {
    Statement st = bd.createStatement(); // bd é a conexão com o banco de dados
    ResultSet rs = st.executeQuery("Colocar a querie aqui");

    // Aqui vai as operações com o resultado. O resultado estará em rs, portanto caso queira escrever o resultado de um select, por exemplo, o código abaixo servirá. buscar resultado de uma operação como insert ou update não ira retornar um resultado.
    while (rs.next()) {
        System.out.println(rs.getString(1)); // Onde 1 é a coluna sendo lida
    }
    // Fim do código exemplo
    
    rs.close();
    st.close();
}
```

## Protocolo
O Protocolo foi definido em sala e segue desta forma:
### Registro de Clientes:
* Cliente para Servidor: REGISTRO <nome_de_usuario>
* Servidor para Cliente (Confirmação): REGISTRO_OK
* Servidor para Cliente (Erro, já existe usuário com o nome, por exemplo): ERRO
<mensagem_de_erro>
### Criação de Salas:
* Cliente para Servidor: CRIAR_SALA <tipo_de_sala: PUBLICA | PRIVADA> <nome_da_sala> [hash(<senha>)] (Para salas privadas, o campo senha é obrigatório)\
* Servidor para Cliente (Confirmação): CRIAR_SALA_OK
* Servidor para Cliente (Erro, já existe sala com o mesmo nome ou ausência de senha para
salas privadas, por exemplo): ERRO <mensagem_de_erro>
### Listar as salas
* Cliente para servidor: LISTAR_SALAS
* Servidor para cliente: SALAS <lista_de_salas>
* Servidor para Cliente (Erro, usuário não está autenticado, por exemplo): ERRO
<mensagem_de_erro>
### Entrada em Salas:
* Cliente para Servidor: ENTRAR_SALA <nome_da_sala> [hash(<senha>)] (Para
salas privadas, o campo senha é obrigatório)
* Servidor para Cliente (Confirmação): ENTRAR_SALA_OK
<lista_usuarios_na_sala>. Ex.: ENTRAR_SALA_OK user1 user_2
* Servidor avisa os demais com ENTROU <nome_da_sala>
<nome_do_usuario>
* Servidor para Cliente (Erro, senha incorreta ou sala não existe, por exemplo): ERRO
<mensagem_de_erro>
### Saída de Salas:
* Cliente para Servidor: SAIR_SALA <nome_da_sala> 
* Servidor para Cliente (Confirmação): SAIR_SALA_OK 
* Servidor avisa os demais com SAIU <nome_da_sala>
<nome_do_usuario>
* Servidor para Cliente (Erro, sala não existe, por exemplo): ERRO
<mensagem_de_erro>
### Envio de Mensagens:
* Cliente para Servidor: ENVIAR_MENSAGEM <nome_da_sala> <mensagem> 
* Servidor para Cliente: MENSAGEM <nome_da_sala>
<nome_do_remetente> <mensagem>
* Servidor para Cliente (Erro, sala não existe, por exemplo):
### ERRO <mensagem_de_erro>
Fechar uma sala
* O administrador da sala envia uma mensagem ao servidor
solicitando o fechamento da sala.
* O servidor confirma o fechamento da sala para o administrador
e notifica todos os membros da sala sobre o fechamento.
* Mensagem do Cliente para o Servidor: FECHAR_SALA <nome_da_sala>
* Mensagem do Servidor para o Cliente (Administrador): FECHAR_SALA_OK
* Servidor para Cliente (Erro, por exemplo, usuário não é o administrador): ERRO
<mensagem_de_erro>
* Mensagem do Servidor para os Membros da Sala: SALA_FECHADA <nome_da_sala>
* Após o fechamento, nenhuma outra mensagem deve enviada pelo
servidor naquela sala.
