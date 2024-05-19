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