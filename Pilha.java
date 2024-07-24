import java.util.LinkedList;

public class Pilha extends EstrategiaLIFO {
    private LinkedList<Senha> pilha; // Pilha de senhas
    private TipoLista tipo; // Tipo de lista (ex: preferencial, normal, etc.)
    private String nomeCliente; // Nome do cliente associado à pilha (opcional)

    // Construtor da classe
    public Pilha(TipoLista tipo) {
        this.pilha = new LinkedList<>(); // Inicializa a pilha
        this.tipo = tipo; // Define o tipo de lista
    }

    // Método para inserir uma nova senha na pilha
    @Override
    public String inserir() {
        Senha novaSenha = new Senha(); // Cria uma nova senha
        novaSenha.gerarSenha(); // Gera uma senha aleatória
        pilha.push(novaSenha); // Adiciona a nova senha no topo da pilha
        return novaSenha.retornarSenha(); // Retorna a senha gerada
    }

    // Método para remover uma senha da pilha (não implementado)
    @Override
    public void remover() {
        // Não implementado
    }

    // Método para chamar a última senha da pilha
    @Override
    public String chamar() {
        if (!pilha.isEmpty()) {
            Senha ultimaSenha = pilha.peek(); // Obtém a última senha sem removê-la da pilha
            ultimaSenha.setChamado(true); // Marca a senha como chamada
            return ultimaSenha.retornarSenha(); // Retorna a senha chamada
        } else {
            return null; // Retorna null se a pilha estiver vazia
        }
    }

    // Método para atender a última senha da pilha
    @Override
    public String atender() {
        if (!pilha.isEmpty()) {
            Senha ultimaSenha = pilha.peek(); // Obtém a última senha sem removê-la da pilha
            if (ultimaSenha.getChamado()) { // Verifica se a senha foi chamada
                ultimaSenha = pilha.pop(); // Remove e retorna a última senha da pilha
                return ultimaSenha.retornarSenha(); // Retorna a senha atendida
            }
        }
        return null; // Retorna null se a pilha estiver vazia ou se a última senha não foi chamada
    }

    // Método para listar as senhas na pilha (não implementado)
    @Override
    public String listar() {
        return ""; // Retorna uma string vazia por enquanto
    }

    // Método para obter a lista de senhas na pilha
    public LinkedList<Senha> getSenhas() {
        return pilha;
    }

    // Método para verificar se a pilha está vazia
    public boolean isEmpty() {
        return pilha.isEmpty();
    }
}
