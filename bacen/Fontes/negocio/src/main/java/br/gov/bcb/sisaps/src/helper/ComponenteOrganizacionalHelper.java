/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ComponenteOrganizacional;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;

public class ComponenteOrganizacionalHelper {

    private static final String BARRA = "/";

    public static ComponenteOrganizacionalVO converterParaVO(ComponenteOrganizacional componente) {
        ComponenteOrganizacionalVO componenteVO = new ComponenteOrganizacionalVO();
        componenteVO.setRotulo(componente.getRotulo().trim());
        componenteVO.setSigla(componente.getSigla().trim());
        List<ComponenteOrganizacional> componentes = componente.getComponentes();

        if (CollectionUtils.isNotEmpty(componentes)) {
            componenteVO.setFilhos(converterParaVO(componentes));
        }

        return componenteVO;
    }

    public static ArrayList<ComponenteOrganizacionalVO> converterParaVO(List<ComponenteOrganizacional> lista) {
        ArrayList<ComponenteOrganizacionalVO> listaVO = new ArrayList<ComponenteOrganizacionalVO>();
        for (ComponenteOrganizacional componente : lista) {
            ComponenteOrganizacionalVO componenteVO = converterParaVO(componente);
            listaVO.add(componenteVO);
        }
        return listaVO;
    }
    
    public static List<ComponenteOrganizacionalVO> montarComponenteOrganizacionalVO(List<String> localizacoes) {
        ArrayList<ComponenteOrganizacionalVO> retorno = new ArrayList<ComponenteOrganizacionalVO>();
        if (CollectionUtils.isNotEmpty(localizacoes)) {
            Map<String, List<ComponenteOrganizacionalVO>> primeiroNivel = 
                    new HashMap<String, List<ComponenteOrganizacionalVO>>();
            Map<String, List<ComponenteOrganizacionalVO>> segundoNivel = 
                    new HashMap<String, List<ComponenteOrganizacionalVO>>();
            Map<String, List<ComponenteOrganizacionalVO>> terceiroNivel = 
                    new HashMap<String, List<ComponenteOrganizacionalVO>>();
            Map<String, List<ComponenteOrganizacionalVO>> quartoNivel = 
                    new HashMap<String, List<ComponenteOrganizacionalVO>>();
            for (String localizacao : localizacoes) {
                String[] niveisLocalizacao = localizacao.split(BARRA);
                ComponenteOrganizacionalVO componentePrimeiroNivel = null;
                ComponenteOrganizacionalVO componenteSegundoNivel = null;
                ComponenteOrganizacionalVO componenteTerceiroNivel = null;
                for (int i = 0; i < niveisLocalizacao.length; i++) {
                    ComponenteOrganizacionalVO componenteVO = criarComponenteOrganizacionalVO(niveisLocalizacao[i]);
                    if (processarPrimeiroNivel(primeiroNivel, i, componenteVO)) {
                        componentePrimeiroNivel = componenteVO;
                    }
                    if (processarSegundoNivel(primeiroNivel, segundoNivel, componentePrimeiroNivel, i, componenteVO)) {
                        componenteSegundoNivel = componenteVO;
                    }
                    if (processarTerceiroNivel(segundoNivel, terceiroNivel, 
                            componenteSegundoNivel, i, componenteVO)) {
                        componenteTerceiroNivel = componenteVO;
                    }
                    processarQuartoNivel(terceiroNivel, quartoNivel, componenteTerceiroNivel, i, componenteVO);
                }
            }
            retorno.addAll(montarLista(primeiroNivel, segundoNivel, terceiroNivel, quartoNivel));
        }
        return retorno;
    }

    private static boolean processarPrimeiroNivel(
            Map<String, List<ComponenteOrganizacionalVO>> primeiroNivel, 
            int i, ComponenteOrganizacionalVO componenteVO) {
        boolean isPrimeiroNivel = false;
        if (i == 0) {
            isPrimeiroNivel = true;
            if (!primeiroNivel.containsKey(componenteVO.getRotulo())) {
                primeiroNivel.put(componenteVO.getRotulo(), new ArrayList<ComponenteOrganizacionalVO>());
            }
        }
        return isPrimeiroNivel;
    }

    private static boolean processarSegundoNivel(
            Map<String, List<ComponenteOrganizacionalVO>> primeiroNivel,
            Map<String, List<ComponenteOrganizacionalVO>> segundoNivel,
            ComponenteOrganizacionalVO componentePrimeiroNivel, int i, 
            ComponenteOrganizacionalVO componenteVO) {
        boolean isSegundoNivel = false;
        if (i == 1) {
            isSegundoNivel = true;
            List<ComponenteOrganizacionalVO> filhos = primeiroNivel.get(componentePrimeiroNivel.getRotulo());
            if (filhos.contains(componenteVO)) {
                componenteVO.setRotulo(componentePrimeiroNivel.getRotulo() + BARRA + componenteVO.getSigla());
            } else {
                componenteVO.setRotulo(componentePrimeiroNivel.getRotulo() + BARRA + componenteVO.getSigla());
                filhos.add(componenteVO);
                segundoNivel.put(componenteVO.getRotulo(), new ArrayList<ComponenteOrganizacionalVO>());
            }
        }
        return isSegundoNivel;
    }

    private static boolean processarTerceiroNivel(
            Map<String, List<ComponenteOrganizacionalVO>> segundoNivel,
            Map<String, List<ComponenteOrganizacionalVO>> terceiroNivel,
            ComponenteOrganizacionalVO componenteSegundoNivel, int i, ComponenteOrganizacionalVO componenteVO) {
        boolean isTerceiroNivel = false;
        if (i == 2) {
            isTerceiroNivel = true;
            List<ComponenteOrganizacionalVO> filhos = segundoNivel.get(componenteSegundoNivel.getRotulo());
            if (filhos.contains(componenteVO)) {
                componenteVO.setRotulo(componenteSegundoNivel.getRotulo() + BARRA + componenteVO.getSigla());
            } else {
                componenteVO.setRotulo(componenteSegundoNivel.getRotulo() + BARRA + componenteVO.getSigla());
                filhos.add(componenteVO);
                terceiroNivel.put(componenteVO.getRotulo(), new ArrayList<ComponenteOrganizacionalVO>());
            }
        }
        return isTerceiroNivel;
    }
    
    private static void processarQuartoNivel(
            Map<String, List<ComponenteOrganizacionalVO>> terceiroNivel,
            Map<String, List<ComponenteOrganizacionalVO>> quartoNivel,
            ComponenteOrganizacionalVO componenteTerceiroNivel, int i, ComponenteOrganizacionalVO componenteVO) {
        if (i == 3) {
            List<ComponenteOrganizacionalVO> filhos = terceiroNivel.get(componenteTerceiroNivel.getRotulo());
            if (filhos.contains(componenteVO)) {
                componenteVO.setRotulo(componenteTerceiroNivel.getRotulo() + BARRA + componenteVO.getSigla());
            } else {
                componenteVO.setRotulo(componenteTerceiroNivel.getRotulo() + BARRA + componenteVO.getSigla());
                filhos.add(componenteVO);
                quartoNivel.put(componenteVO.getRotulo(), new ArrayList<ComponenteOrganizacionalVO>());
            }
        }
    }
    
    private static List<ComponenteOrganizacionalVO> montarLista(
            Map<String, List<ComponenteOrganizacionalVO>> primeiroNivel,
            Map<String, List<ComponenteOrganizacionalVO>> segundoNivel,
            Map<String, List<ComponenteOrganizacionalVO>> terceiroNivel,
            Map<String, List<ComponenteOrganizacionalVO>> quartoNivel) {
        List<ComponenteOrganizacionalVO> retorno = new ArrayList<ComponenteOrganizacionalVO>();
        for (String sigla : primeiroNivel.keySet()) {
            ComponenteOrganizacionalVO componentePrimeiroNivel = criarComponenteOrganizacionalVO(sigla);
            componentePrimeiroNivel.setFilhos(primeiroNivel.get(componentePrimeiroNivel.getSigla()));
            if (CollectionUtils.isNotEmpty(componentePrimeiroNivel.getFilhos())) {
                for (ComponenteOrganizacionalVO componenteSegundoNivel : componentePrimeiroNivel.getFilhos()) {
                    componenteSegundoNivel.setFilhos(segundoNivel.get(componenteSegundoNivel.getRotulo()));
                    verificarSegundoNivel(terceiroNivel, quartoNivel, componenteSegundoNivel);
                }
            }
            retorno.add(componentePrimeiroNivel);
        }
        return retorno;
    }

    private static void verificarSegundoNivel(Map<String, List<ComponenteOrganizacionalVO>> terceiroNivel,
            Map<String, List<ComponenteOrganizacionalVO>> quartoNivel, ComponenteOrganizacionalVO componenteSegundoNivel) {
        if (CollectionUtils.isNotEmpty(componenteSegundoNivel.getFilhos())) {
            for (ComponenteOrganizacionalVO componenteTerceiroNivel : componenteSegundoNivel.getFilhos()) {
                componenteTerceiroNivel.setFilhos(terceiroNivel.get(componenteTerceiroNivel.getRotulo()));
                verificarTerceiroNivel(quartoNivel, componenteTerceiroNivel);
            }
        }
    }

    private static void verificarTerceiroNivel(Map<String, List<ComponenteOrganizacionalVO>> quartoNivel,
            ComponenteOrganizacionalVO componenteTerceiroNivel) {
        if (CollectionUtils.isNotEmpty(componenteTerceiroNivel.getFilhos())) {
            for (ComponenteOrganizacionalVO componenteQuartoNivel 
                    : componenteTerceiroNivel.getFilhos()) {
                componenteQuartoNivel.setFilhos(quartoNivel.get(componenteQuartoNivel.getRotulo()));
            }
        }
    }

    private static ComponenteOrganizacionalVO criarComponenteOrganizacionalVO(String sigla) {
        ComponenteOrganizacionalVO componenteVO = new ComponenteOrganizacionalVO();
        componenteVO.setSigla(sigla);
        componenteVO.setRotulo(sigla);
        return componenteVO;
    }

}
