from pathlib import Path

# Raiz do projeto atual
RAIZ_PROJETO = Path.cwd()

# Nome do arquivo consolidado
ARQUIVO_SAIDA = "consolidado_fertintelligence.txt"

# Pastas que serão ignoradas
PASTAS_IGNORADAS = {
    "target",
    ".git",
    ".idea",
    ".vscode",
    "node_modules",
    "build",
    "out",
}

# Extensões que serão consolidadas
EXTENSOES_PERMITIDAS = {
    ".java",
    ".properties",
    ".yml",
    ".yaml",
    ".xml",
    ".md",
    ".gradle",
    ".txt",
}

arquivos_processados = 0

with open(ARQUIVO_SAIDA, "w", encoding="utf-8") as arquivo_saida:

    for arquivo in sorted(RAIZ_PROJETO.rglob("*")):

        # Ignora diretórios
        if not arquivo.is_file():
            continue

        # Ignora pastas específicas
        if any(parte in PASTAS_IGNORADAS for parte in arquivo.parts):
            continue

        # Ignora o próprio consolidado
        if arquivo.name == ARQUIVO_SAIDA:
            continue

        # Filtra extensões
        if arquivo.suffix.lower() not in EXTENSOES_PERMITIDAS:
            continue

        caminho_relativo = arquivo.relative_to(RAIZ_PROJETO)

        try:
            conteudo = arquivo.read_text(encoding="utf-8")
        except UnicodeDecodeError:
            try:
                conteudo = arquivo.read_text(encoding="latin-1")
            except Exception:
                print(f"Erro ao ler: {caminho_relativo}")
                continue

        arquivo_saida.write("---\n")
        arquivo_saida.write(f"[{caminho_relativo}]\n")
        arquivo_saida.write("---\n")
        arquivo_saida.write(conteudo)
        arquivo_saida.write("\n---\n\n")

        arquivos_processados += 1

print(f"\nConsolidação finalizada.")
print(f"Arquivos processados: {arquivos_processados}")
print(f"Arquivo gerado: {ARQUIVO_SAIDA}")