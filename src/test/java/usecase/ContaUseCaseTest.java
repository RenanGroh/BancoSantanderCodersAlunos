package usecase;

import domain.exception.SaldoInvalidoException;
import domain.exception.ValorNegativoException;
import domain.gateway.ContaGateway;
import domain.model.Cliente;
import domain.model.Conta;
import domain.usecase.ContaUseCase;
import infra.gateway.ContaGatewayLocal;
import org.junit.jupiter.api.*;


public class ContaUseCaseTest {

    private ContaUseCase contaUseCase;
    private ContaGateway contaGateway;

    // @BeforeClass - antes da classe ser instanciada
    @BeforeAll
    public static void beforeClass() {
        // Subir banco
        // Preparar alguma config
        System.out.println("Before class");
    }

    // @Before - antes de CADA teste
    @BeforeEach
    public void before() {
        System.out.println("before");

        contaGateway = new ContaGatewayLocal();
        contaUseCase = new ContaUseCase(contaGateway);

        Cliente cliente1 = new Cliente("Ana", "111.111.111.11");
        Conta conta1 = new Conta("1", cliente1);

        Cliente cliente2 = new Cliente("Carla", "222.222.222.22");
        Conta conta2 = new Conta("2", cliente2);

        contaGateway.save(conta1);
        contaGateway.save(conta2);
    }

    // @After
    @AfterEach
    public void after() {
        System.out.println("After");
    }

    // @AfterClass
    @AfterAll
    public static void afterClass() {
        System.out.println("After class");
    }

    @Test
    public void deveTransferirCorretamenteEntreDuasContas() throws Exception {
        // Mocks

        System.out.println("deveTransferirCorretamenteEntreDuasContas");
        // Given - Dado
        contaUseCase.depositar("1", 100.0);

        // When - Quando
        contaUseCase.transferir("1", "2", 20.0);

        // Then - Entao
        // - Valor esperado - Valor atual
        Double valorEsperadoConta1 = 80.0;
        Conta conta1DB = contaGateway.findById("1");
        Assertions.assertEquals(valorEsperadoConta1, conta1DB.getSaldo());

        Double valorEsperadoConta2 = 20.0;
        Conta conta2DB = contaGateway.findById("2");
        Assertions.assertEquals(valorEsperadoConta2, conta2DB.getSaldo());
    }

    @Test
    public void testeTransferirDeveLancarExcecaoQuandoContaOrigemForNull() {
        String idOrigem = "203940";
        Conta contaDestino = contaUseCase.buscarConta("2");
        Assertions.assertThrows(Exception.class, () -> contaUseCase.transferir(idOrigem, contaDestino.getId(), 100.00), "Conta origem invalida - [id: " + idOrigem + "]");
    }

    @Test
    public void testeTransferirDeveLancarExcecaoQuandoContaDestinoForNull() {
        Conta contaOrigem = contaUseCase.buscarConta("1");
        String idDestino = "203940";
        Assertions.assertThrows(Exception.class, () -> contaUseCase.transferir(contaOrigem.getId(), idDestino , 100.00),"Conta origem invalida - [id: " + idDestino + "]");
    }

    @Test
    public void deveDepositarCorretamente() throws Exception {
        System.out.println("deveDepositarCorretamente");
        // Given -  Dado

        // When - Quando
        contaUseCase.depositar("1", 10.0);
        Conta conta1 = contaGateway.findById("1");

        // Then
        Double valorEsperado = 10.0;
        Assertions.assertEquals(valorEsperado, conta1.getSaldo());
    }

    @Test
    public void testarDepositarDeveLancarExcecaoQuandoContaForNull() {
        Assertions.assertThrows(Exception.class, () -> contaUseCase.depositar("10292", 100.00));
    }

    @Test
    public void testeExemplo1() {
        System.out.println("testeExemplo");
    }

    @Test
    public void testandoSeContaEstaSendoCriada() {

        Cliente clienteNovo = new Cliente("Poucos mas Muito Loucos", "666.666.666-69");
        Conta contaNova = new Conta("69", clienteNovo);

        contaUseCase.criarConta(contaNova);
        Assertions.assertEquals(contaNova, contaGateway.findById("69"));
    }

    @Test
    public void testarBuscaDeConta() {
        Cliente clienteNovo = new Cliente("Poucos mas Muito Loucos", "666.666.666-69");
        Conta contaNova = new Conta("69", clienteNovo);

        contaUseCase.criarConta(contaNova);

        Conta contaEncontrada = contaUseCase.buscarConta("69");
        Assertions.assertNotNull(contaEncontrada);
//        System.out.println("CONTA ENCONTRADA ===> " + contaEncontrada);
    }

    @Test
    public void testarAdicionarSaldoEmprestimoDeveLancarExcecaoQuandoContaForNula() {
        Assertions.assertThrows(Exception.class, () -> contaUseCase.adicionarSaldoEmprestimo("300",100d));
    }

    @Test
    public void testarAdicionarSaldoEmprestimoDeveLancarExcecaoQuandoValorForNegativo() {
        Cliente cliente = new Cliente("Cliente Teste", "122.222.333-10");
        Conta conta = new Conta("400", cliente);

        contaUseCase.criarConta(conta);

        Assertions.assertThrows(ValorNegativoException.class, () -> contaUseCase.adicionarSaldoEmprestimo("400",-100d));
    }


    @Test
    public void testarEmprestimoComSaldoInsuficiente() throws Exception {
        Cliente clienteNovo = new Cliente("Poucos mas Muito Loucos", "666.666.666-69");
        Conta contaNova = new Conta("69", clienteNovo);
        contaUseCase.criarConta(contaNova);

        contaNova.adicionarSaldoParaEmprestimo(100d);

        Assertions.assertThrows(SaldoInvalidoException.class, () -> contaUseCase.emprestimo("69", 300d));
    }

    @Test
    public void testarEmprestimoComSaldoBoladao() throws Exception {
        Cliente clienteNovo = new Cliente("Poucos mas Muito Loucos", "666.666.666-69");
        Conta contaNova = new Conta("69", clienteNovo);
        contaUseCase.criarConta(contaNova);

        contaNova.adicionarSaldoParaEmprestimo(200000d);
        contaUseCase.emprestimo("69", 100000d);
        Assertions.assertEquals(100000d, contaNova.getSaldoDisponivelParaEmprestimo(),0.000001);
    }

    @Test
    public void testarEmprestimoContaInvalida() throws Exception {
        Assertions.assertThrows(Exception.class, () -> contaUseCase.emprestimo("32", 300d));
    }

    @Test
    public void testarEmprestimoComSaldoExato() throws Exception {
        Cliente cliente = new Cliente("Valido Cliente", "123.456.789-10");
        Conta conta = new Conta("123", cliente);
        conta.adicionarSaldoParaEmprestimo(5000d);
        contaUseCase.criarConta(conta);

        contaUseCase.emprestimo("123", 5000d);
        Assertions.assertEquals(0d, conta.getSaldoDisponivelParaEmprestimo(), 0.001);
    }



}
