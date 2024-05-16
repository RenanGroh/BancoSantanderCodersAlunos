package model;

import domain.exception.SaldoInvalidoException;
import domain.model.Cliente;
import domain.model.Conta;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ContaTest {

    // Rules - Outra forma de testar exceptions (Extra) - @Rule

    @Test
    public void deveLancarExceptionCasoValorSolicitadoSejaMaiorQueValorDisponivel() {
        // Given
        Cliente cliente1 = new Cliente("Ana", "123.123.123-12");
        Conta conta1 = new Conta("1", cliente1);
        // Zero disponivel para emprestimo

        // When
        try {
            conta1.removerSaldoParaEmprestimo(100.0);
            Assertions.fail("Nao lancou nenhuma exception");
        } catch (Exception e) {
            Assertions.assertEquals("Saldo para emprestimo inferior ao solicitado", e.getMessage());
        }
    }

    @Test//(expected = Exception.class)
    public void deveLancarExceptionCasoValorSolicitadoSejaMaiorQueValorDisponivel_2() {
        // Given
        Cliente cliente1 = new Cliente("Ana", "123.123.123-12");
        Conta conta1 = new Conta("1", cliente1);

        // When
        Assertions.assertThrows(Exception.class,() -> conta1.removerSaldoParaEmprestimo(100.0));
    }

    @Test
    public void deveLancarExceptionCasoValorSolicitadoSejaMaiorQueValorDisponivel_3() {
        Cliente cliente1 = new Cliente("Ana", "123.123.123-12");
        Conta conta1 = new Conta("1", cliente1);

        Throwable throwable = Assertions.assertThrows(SaldoInvalidoException.class, () -> conta1.removerSaldoParaEmprestimo(100.0));
        Assertions.assertEquals("Saldo para emprestimo inferior ao solicitado", throwable.getMessage());
    }

}
