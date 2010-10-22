#!/bin/sh

#Running twice to ensured correct generation.
pdflatex -interaction=nonstopmode report.tex
pdflatex -interaction=nonstopmode report.tex

