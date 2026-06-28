# Contrato de unidade da Recomendacao Direta

A resposta de `direct-recommendation/get-by-recommendation` inclui metadados no nivel da Recomendacao Direta para orientar a exibicao da coluna operacional:

- `dose_unit_mode = LINEAR_METER`: renderizar a coluna `g/m linear`.
- `dose_unit_mode = PIT`: renderizar a coluna `g/cova`.
- `dose_unit_mode = INSUFFICIENT_DATA`: nao ha dados suficientes para decidir uma unidade aplicavel com seguranca.

Os campos textuais legados `laudo_tecnico` e `conteudo` continuam em Markdown. Nas tabelas geradas pelo backend, apenas a coluna aplicavel e exibida; ela recebe valor calculado quando houver dados suficientes ou `Nao calculado por falta de dados.` quando a conversao nao puder ser feita com seguranca.
