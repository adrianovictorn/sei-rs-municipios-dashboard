package br.gov.rs.seimunicipios.etapa;

import java.util.List;

/**
 * Etapas e itens de checklist padrao extraidos do Plano de Trabalho de Kick-off dos
 * Municipios, usados para popular um municipio recem-cadastrado. Totalmente editavel
 * depois pelo usuario (adicionar/editar/remover etapas e itens).
 */
public final class EtapaSeed {

    private EtapaSeed() {
    }

    public record ItemTemplate(String descricao) {
    }

    public record EtapaTemplate(String nome, String descricao, List<ItemTemplate> itens) {
    }

    public static List<EtapaTemplate> padrao() {
        return List.of(
                new EtapaTemplate("Apresentação", "Kickoff e diagnóstico inicial do município.", List.of(
                        new ItemTemplate("Kickoff e definição da Equipe de Trabalho"),
                        new ItemTemplate("Diagnóstico técnico e negocial"),
                        new ItemTemplate("Cronograma macro")
                )),
                new EtapaTemplate("Planejamento", "Estrutura organizacional e preparação para a implantação.", List.of(
                        new ItemTemplate("Estrutura organizacional (organograma e equipe)"),
                        new ItemTemplate("Validação de acessos (planilhas e usuários)"),
                        new ItemTemplate("Preparação para implantação")
                )),
                new EtapaTemplate("Capacitação e Comunicação", "Treinamentos e materiais de apoio.", List.of(
                        new ItemTemplate("Capacitação específica (público alvo)"),
                        new ItemTemplate("Treinamentos online"),
                        new ItemTemplate("Disponibilização de cartilhas e vídeos práticos")
                )),
                new EtapaTemplate("Mapeamento de Fluxo e Gestão Documental", "Processos, gargalos e regras documentais.", List.of(
                        new ItemTemplate("Mapeamento de fluxos (processos, gargalos, melhorias)"),
                        new ItemTemplate("Gestão documental (padronização, regras, normas legais)")
                )),
                new EtapaTemplate("Definição do Suporte", "Escopo e canal de suporte interno.", List.of(
                        new ItemTemplate("Apoio na definição do suporte interno"),
                        new ItemTemplate("Indicação do canal geral de suporte"),
                        new ItemTemplate("Apoio na definição do escopo do suporte")
                )),
                new EtapaTemplate("Go-live", "Disponibilização assistida do sistema.", List.of(
                        new ItemTemplate("Go-live assistido"),
                        new ItemTemplate("Ajustes finais antes da disponibilização geral"),
                        new ItemTemplate("Acompanhamento do Especialista SEI")
                )),
                new EtapaTemplate("Monitoramento e Avaliação", "Métricas, resultados e melhorias contínuas.", List.of(
                        new ItemTemplate("Monitorar uso (métricas, gargalos)"),
                        new ItemTemplate("Avaliar resultados (indicadores, feedback)"),
                        new ItemTemplate("Propor melhorias (lições aprendidas, expansão)")
                ))
        );
    }
}
