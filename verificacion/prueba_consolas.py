from pathlib import Path
from tempfile import TemporaryDirectory
import subprocess

cp = str(Path(__file__).resolve().parents[1] / 'bin')
with TemporaryDirectory() as d:
    def run(cls, entrada):
        r = subprocess.run(['java', '-ea', '-cp', cp, 'seneca.consola.' + cls], cwd=d, input=entrada, text=True, capture_output=True, check=True)
        return r.stdout
    out = run('ConsolaAdmin', 'admin\nclave\nadmin\nclave\n9\n1\n20\nsocio\nclave\n9\n2\ncoach\nclave\nCODE\n1\nt1\nTorneo\n2030-01-07\n4\ntenis\nintermedio\n1\n0\n0.15\n3\n1\n0\n')
    assert 'Error:' not in out
    out = run('ConsolaSocio', 'socio\nclave\n99\n2\n1\n0\n')
    assert 'Inscrito:' in out and 'Ingrese un entero' in out
    out = run('ConsolaEmpleado', 'coach\nclave\n2\n1\n0\n')
    assert 'Inscrito:' in out
    out = run('ConsolaAdmin', 'admin\nclave\n4\n1\n5\n1\n1\n2\n0\n6\n1\n7\n1\n0\n')
    assert 'FINALIZADO' in out and 'Error:' not in out
    out = run('ConsolaSocio', 'socio\nclave\n4\n0\n')
    assert 'disponible=true' in out
    out = run('ConsolaEmpleado', 'socio\nclave\nsocio\nclave\nsocio\nclave\n')
    assert out.count('Credenciales o rol incorrectos.') == 3
print('Flujo de las tres consolas: OK')
