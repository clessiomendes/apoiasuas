import org.apoiasuas.cidadao.*;

//final def hql = "select a, a.familia from Cidadao a inner join fetch a.familia left join fetch a.familia.tecnicoReferencia where 1=1 "
final def hql = "select a from Cidadao a left join fetch a.familia.tecnicoReferencia join a.familia.membros membrosFamiliares  where 1=1  and a.servicoSistemaSeguranca = 1 and lower(remove_acento(membrosFamiliares.nomeCompleto)) like remove_acento('%lucas%') and lower(remove_acento(a.nomeCompleto)) like remove_acento('%anne%')"

Familia.withNewSession { session ->

def a = Familia.executeQuery(hql).iterator();
def i = 0;
while (a.hasNext() && i++ <5) {
   def cidadao = a.next();
   if (cidadao instanceof Cidadao)
       println("cad ${cidadao.familia.id}, ${cidadao.nomeCompleto}")
   else
       println(cidadao)
}
    
}
