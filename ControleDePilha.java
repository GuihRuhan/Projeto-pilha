import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;

public class ControleDePilha extends JFrame {
    // Variáveis de interface
    private HashMap<TipoLista, Pilha> pilhas;
    private JTextField nomeDoCliente;
    private JComboBox<TipoLista> tipoDeSenha;
    private JButton chamarButton;
    private JButton finalizarButton;
    private JButton atenderButton;
    private JButton adicionarButton;
    private JButton limparButton;
    private JTable tabelaSenhas;
    private DefaultTableModel tabelaModelo;
    private HashMap<TipoLista, LinkedList<String>> nomesClientes;
    private boolean pessoaSendoChamada = false;

    private static final TipoLista[] ordemPrioridade = {
            TipoLista.URGENTE,
            TipoLista.IDOSO80,
            TipoLista.PREFERENCIAL,
            TipoLista.IDOSO,
            TipoLista.VIP,
            TipoLista.NORMAL
    };

    // Construtor da classe
    public ControleDePilha() {
        // Inicialização das estruturas de dados
        pilhas = new HashMap<>();
        nomesClientes = new HashMap<>();
        for (TipoLista tipo : TipoLista.values()) {
            pilhas.put(tipo, new Pilha(tipo));
            nomesClientes.put(tipo, new LinkedList<>());
        }

        // Configurações da janela
        setTitle("Sistema de Controle de Atendimento - Consultório Dr. No Problem, CRM 01.892");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Configuração do painel de entrada
        JPanel painelEntrada = new JPanel();
        painelEntrada.setLayout(new GridLayout(3, 2));
        painelEntrada.add(new JLabel("Nome do Cliente:"));
        nomeDoCliente = new JTextField();
        painelEntrada.add(nomeDoCliente);
        painelEntrada.add(new JLabel("Tipo de Senha:"));
        tipoDeSenha = new JComboBox<>(TipoLista.values());
        painelEntrada.add(tipoDeSenha);
        adicionarButton = new JButton("Adicionar Senha");
        painelEntrada.add(adicionarButton);
        limparButton = new JButton("Limpar");
        painelEntrada.add(limparButton);
        add(painelEntrada, BorderLayout.NORTH);

        // Configuração do painel de controle
        JPanel painelControle = new JPanel();
        painelControle.setLayout(new FlowLayout());
        chamarButton = new JButton("Chamar Senha");
        painelControle.add(chamarButton);
        atenderButton = new JButton("Atender Senha");
        painelControle.add(atenderButton);
        finalizarButton = new JButton("Finalizar");
        painelControle.add(finalizarButton);
        add(painelControle, BorderLayout.SOUTH);

        // Configuração da tabela de senhas
        tabelaModelo = new DefaultTableModel(new String[]{"Tipo", "Senha", "Nome", "Chamado"}, 0);
        tabelaSenhas = new JTable(tabelaModelo);
        add(new JScrollPane(tabelaSenhas), BorderLayout.CENTER);

        // Configuração das ações dos botões
        configurarAcoes();

        setVisible(true);
    }

    // Configura as ações dos botões
    private void configurarAcoes() {
        adicionarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inserirSenha();
            }
        });

        limparButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparCampos();
            }
        });

        chamarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chamarSenha();
            }
        });

        atenderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atenderSenha();
            }
        });

        finalizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encerrarPrograma();
            }
        });
    }

    // Insere uma senha na pilha
    private void inserirSenha() {
        TipoLista tipo = (TipoLista) tipoDeSenha.getSelectedItem();
        if (tipo != null) {
            String nome = nomeDoCliente.getText();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, insira o nome do cliente.");
                return;
            }

            Pilha pilha = pilhas.get(tipo);
            String senhaGerada = pilha.inserir();
            if (senhaGerada != null) {
                nomesClientes.get(tipo).add(nome);
                atualizarTabelaSenhas();
                JOptionPane.showMessageDialog(this, "Senha gerada: " + senhaGerada);
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao gerar a senha.");
            }
        }
    }

    // Limpa os campos de nome do cliente e tipo de senha
    private void limparCampos() {
        nomeDoCliente.setText("");
        tipoDeSenha.setSelectedIndex(0);
    }

    // Chama a próxima senha para atendimento
    private void chamarSenha() {
        if (pessoaSendoChamada) {
            JOptionPane.showMessageDialog(this, "Uma pessoa já está sendo chamada. Espere ela ser atendida.");
            return;
        }

        Pilha pilhaChamada = getPilhaPossuiSenhaChamada();
        if (pilhaChamada != null) {
            JOptionPane.showMessageDialog(this, "Já existe uma senha chamada.");
            return;
        }

        String senhaChamada = null;
        for (TipoLista tipo : ordemPrioridade) {
            Pilha pilha = pilhas.get(tipo);
            if (!pilha.isEmpty()) {
                senhaChamada = pilha.chamar();
                pessoaSendoChamada = true;
                atualizarTabelaSenhas();
                JOptionPane.showMessageDialog(this, "Senha chamada: " + senhaChamada);
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Não há senhas para chamar.");
    }

    // Retorna a pilha que possui uma senha chamada
    private Pilha getPilhaPossuiSenhaChamada(){
        for (TipoLista tipo : ordemPrioridade){
            Pilha pilha = pilhas.get(tipo);
            if (!pilha.isEmpty()){
                for (Senha senha : pilha.getSenhas()){
                    if (senha.getChamado()){
                        return pilha;
                    }
                }
            }
        }
        return null;
    }

    // Atende a senha chamada
    private void atenderSenha() {
        if (!pessoaSendoChamada) {
            JOptionPane.showMessageDialog(this, "Nenhuma pessoa está sendo chamada.");
            return;
        }

        for (int i = TipoLista.values().length - 1; i >= 0; i--) {
            TipoLista tipo = TipoLista.values()[i];
            Pilha pilha = pilhas.get(tipo);

            if (!pilha.isEmpty()) {
                LinkedList<Senha> senhas = pilha.getSenhas();
                for (int j = senhas.size() - 1; j >= 0; j--) {
                    Senha senha = senhas.get(j);
                    if (senha.getChamado()) {
                        senhas.remove(j);
                        nomesClientes.get(tipo).removeFirst();
                        pessoaSendoChamada = false;
                        atualizarTabelaSenhas();
                        JOptionPane.showMessageDialog(this, "Senha atendida: " + senha.retornarSenha());
                        return;
                    }
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Não há senhas chamadas para atender.");
    }

    // Atualiza a tabela de senhas
    private void atualizarTabelaSenhas() {
        tabelaModelo.setRowCount(0);
        for (TipoLista tipo : TipoLista.values()) {
            Pilha pilha = pilhas.get(tipo);
            LinkedList<Senha> senhas = pilha.getSenhas();
            LinkedList<String> nomes = nomesClientes.get(tipo);

            Color cor = Color.BLACK;
            switch (tipo) {
                case IDOSO:
                    cor = Color.RED;
                    break;
                case IDOSO80:
                    cor = Color.BLUE;
                    break;
                case NORMAL:
                    cor = Color.GREEN;
                    break;
                case PREFERENCIAL:
                    cor = Color.ORANGE;
                    break;
                case URGENTE:
                    cor = Color.YELLOW;
                    break;
                case VIP:
                    cor = Color.MAGENTA;
                    break;
                default:
                    cor = Color.BLACK;
                    break;
            }

            int index = 0;
            for (Senha senha : senhas) {
                String nomeCliente = nomes.size() > index ? nomes.get(index) : "";
                tabelaModelo.addRow(new Object[]{
                        tipo.name(),
                        "<html><font color='" + String.format("#%06X", cor.getRGB() & 0xFFFFFF) + "'>" + senha.retornarSenha() + "</font></html>",
                        nomeCliente,
                        senha.getChamado() ? "Sim" : "Não"
                });
                index++;
            }
        }
    }

    // Encerra o programa
    private void encerrarPrograma() {
        System.exit(0);
    }

    // Método principal
    public static void main(String[] args) {
        new ControleDePilha();
    }
}
