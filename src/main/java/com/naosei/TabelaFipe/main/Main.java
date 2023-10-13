package com.naosei.TabelaFipe.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.naosei.TabelaFipe.model.Dados;
import com.naosei.TabelaFipe.model.Modelo;
import com.naosei.TabelaFipe.model.Veiculo;
import com.naosei.TabelaFipe.service.ConsumoApi;
import com.naosei.TabelaFipe.service.ConverteDados;

import java.util.List;
import java.util.Scanner;

public class Main {
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConverteDados conversorDados = new ConverteDados();
    private final Scanner scanner = new Scanner(System.in);

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private String endereco;

    public void exibeMenu() throws JsonProcessingException {
        System.out.println("**** VEÍCULOS ****");
        System.out.println("""
                - Carro
                - Moto
                - Caminhão""");
        System.out.print("Escolha uma das opções acima: ");
        String tipoVeiculo = formatTipoVeiculo(scanner.nextLine()) + "/marcas/";

        endereco = URL_BASE + tipoVeiculo;
        String json = consumoApi.obterDados(endereco);
        List<Dados> dadosVeiculos = conversorDados.obterLista(json, Dados.class);

        System.out.println("Listagem das marcas");
        dadosVeiculos.forEach(v -> System.out.println(" - Código: " + v.codigo() + " | " + "Marca: " + v.nome()));

        System.out.print("Digite o código do veículo desejado: ");
        var codigoVeiculo = scanner.nextLine() + "/modelos/";
        endereco = URL_BASE + tipoVeiculo + codigoVeiculo;
        json = consumoApi.obterDados(endereco);
        Modelo dadosModelos = conversorDados.obterDados(json, Modelo.class);
        System.out.println("Modelos encontrados");
        dadosModelos.modelos()
                .forEach(m -> System.out.println(" - Código: " + m.codigo() + " | " + "Modelo: " + m.nome()));

        System.out.print("Digite o nome do modelo desejado: ");
        var nomeVeiculo = scanner.nextLine();
        List<Dados> modelosFiltrados = dadosModelos.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .toList();

        System.out.println("Resultados para " + "\"" + nomeVeiculo + "\"");
        modelosFiltrados.forEach(m -> System.out.println(" - Código: " + m.codigo() + " | " + "Nome: " + m.nome()));

        System.out.print("Digite o código do modelo desejado: ");
        var codigoModelo = scanner.nextLine() + "/anos/";
        endereco = URL_BASE + tipoVeiculo + codigoVeiculo + codigoModelo;
        json = consumoApi.obterDados(endereco);
        List<Dados> anos = conversorDados.obterLista(json, Dados.class);

        for (int i = 0; i < anos.size(); i++) {
            var anoModelo = anos.get(i).codigo();
            endereco = URL_BASE + tipoVeiculo + codigoVeiculo + codigoModelo + anoModelo;
            json = consumoApi.obterDados(endereco);
            Veiculo veiculo = conversorDados.obterDados(json, Veiculo.class);

            System.out.printf("""
                                     
                    Marca: %s
                    Modelo: %s
                    Ano: %s
                    Valor: %s%n""", veiculo.marca(), veiculo.modelo(), veiculo.ano(), veiculo.valor());
        }

        System.out.println("\nFim da execução");
    }

    private String formatTipoVeiculo(String tipoVeiculo) {
        String tipoFormatado;
        if (tipoVeiculo.contains("carr")) {
            tipoFormatado = "carros";
        } else if (tipoVeiculo.contains("mot")) {
            tipoFormatado = "motos";
        } else {
            tipoFormatado = "caminhoes";
        }

        return tipoFormatado;
    }
}
