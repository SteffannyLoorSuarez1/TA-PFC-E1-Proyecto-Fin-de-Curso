# SREI — Sistema de Registro de Eventos Interculturales

Proyecto Fin de Curso de Aplicaciones Distribuidas. Primera entrega: TA-PFC-E1.

## Documento

- Fuente principal: [docs/main.tex](docs/main.tex).
- Informe: [docs/main.pdf](docs/main.pdf).
- Secciones: `docs/secciones/`.
- Bibliografía: `docs/referencias/`.
- Diagramas: `docs/diagramas/`; decisión de arquitectura: `docs/adr/ADR-001-estilo.md`.
- Grabaciones originales: `evidencias/levantamiento/entrevistas/`.
- Capturas de ejecución: `evidencias/ejecucion/`.

## Compilación

Requiere una distribución LaTeX con pdfLaTeX y Biber, y los paquetes utilizados en `docs/main.tex` (babel español, lmodern, geometry, setspace, graphicx, float, booktabs, longtable, etoolbox, array, enumitem, csquotes, hyperref y biblatex con estilo IEEE).

Desde la raíz del repositorio:

```powershell
Set-Location docs
pdflatex -interaction=nonstopmode -halt-on-error main.tex
biber main
pdflatex -interaction=nonstopmode -halt-on-error main.tex
pdflatex -interaction=nonstopmode -halt-on-error main.tex
```

El resultado es `docs/main.pdf`. Los auxiliares de compilación están excluidos por `.gitignore`.

## Colaboración

- [Historial de commits](https://github.com/SteffannyLoorSuarez1/TA-PFC-E1-Proyecto-Fin-de-Curso/commits/).
- [Pull Requests](https://github.com/SteffannyLoorSuarez1/TA-PFC-E1-Proyecto-Fin-de-Curso/pulls).
- [Issues](https://github.com/SteffannyLoorSuarez1/TA-PFC-E1-Proyecto-Fin-de-Curso/issues).
- [Issue 8: arquitectura y calidad](https://github.com/SteffannyLoorSuarez1/TA-PFC-E1-Proyecto-Fin-de-Curso/issues/8), asignado a Washington Apunte y cerrado.

Los enlaces individuales a commits se incluyen en el registro de aportes del informe.
