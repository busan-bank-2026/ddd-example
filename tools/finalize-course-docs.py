"""Write chapter documents from the verified teaching commits."""
from pathlib import Path
import re
import subprocess

ROOT = Path(__file__).resolve().parents[1]
P = ROOT / '.projectcode'
CHAPTERS = ['free-shipping', 'order-events', 'optimistic-lock', 'bounded-contexts']
def git(*args):
    return subprocess.check_output(['git', '-c', 'core.quotepath=false', *args], cwd=ROOT).decode('utf-8').strip()
def write(path, text):
    path.write_text(text.strip() + '\n', encoding='utf-8')
def field(text, name):
    m = re.search(r'^' + name + r': (.+)$', text, re.M)
    return m[1] if m else ''

steps = []
for commit in git('rev-list', '--reverse', 'lecture').splitlines():
    msg = git('show', '-s', '--format=%B', commit)
    chapter = field(msg, 'Chapter')
    if chapter not in CHAPTERS:
        continue
    files = git('diff-tree', '--no-commit-id', '--name-status', '-r', '--no-renames', commit, '--', 'ddd').splitlines()
    steps.append(dict(commit=commit, chapter=chapter, task=field(msg,'Task'), title=msg.splitlines()[0].split('] ',1)[-1], source=field(msg,'Source'), files=files))

why = {
('free-shipping','T1'): ('S1, S2, S4', '업무 조건에 이름을 붙이고 참 거짓과 배송비 계산의 역할을 나눈다'),
('free-shipping','T2'): ('S3, S4', '제주 제외 조건을 같은 명세에 더해 두 조건을 한눈에 읽는다'),
('order-events','T1'): ('S1, S4', '재고를 가진 상품부터 만들고 주문 전후 값을 확인한다'),
('order-events','T2'): ('S2, S3, S4', '주문 저장 뒤 재고가 바뀌는 가장 짧은 흐름을 먼저 배운다'),
('optimistic-lock','T1'): ('S1', '오래된 주문을 저장하면 앞 변경이 덮이는 문제를 먼저 실행한다'),
('optimistic-lock','T2'): ('S2, S4', '꺼낸 주문이 몇 번째 저장을 본 것인지 버전으로 기억한다'),
('optimistic-lock','T3'): ('S3, S4', '오래된 버전의 저장을 거절해 앞 변경을 지킨다'),
('bounded-contexts','T1'): ('S1', '업무의 책임과 모델이 통하는 경계를 먼저 정한다'),
('bounded-contexts','T3'): ('S2', '회원 이름과 등급을 맡는 모델을 회원 컨텍스트로 옮긴다'),
('bounded-contexts','T4'): ('S2', '현재 상품 가격과 재고를 상품 컨텍스트로 모은다'),
('bounded-contexts','T5'): ('S1, S8', '패키지를 나눈 뒤에도 남아 있는 컨텍스트 사이 의존을 적는다'),
('bounded-contexts','T7'): ('S3, S4, S5, S7, S8', '각 컨텍스트의 조회와 실행 순서를 응용 서비스가 맡는다'),
('bounded-contexts','T8'): ('S6, S7, S8', '주문 접수 사실과 그 사실에 대한 상품의 반응을 분리한다'),
}
fixes = {
 'free-shipping': ('T4','무료 배송 조건을 한 명세로 합치기','T2','7574f96'),
 'order-events': ('T5','이벤트 대신 주문 뒤 재고 바로 줄이기','T1, T2, T3','170e451'),
 'optimistic-lock': ('T5','직접 재고 감소 흐름에서 낙관적 잠금 유지하기','T1, T2, T3','170e451'),
 'bounded-contexts': ('T9','서비스와 즉시 처리 이벤트로 강의 마무리하기','T1, T3, T4, T5','170e451'),
}
for chapter in CHAPTERS:
    selected = [s for s in steps if s['chapter'] == chapter]
    assert selected, chapter
    tasks = [f'# {chapter} 작업', '', '- [x] 테스트 작성']
    summary = [f'# {chapter} 코드 요약', '', '현재 따라치기 이력에 고침을 녹인 코드다.', 'Source는 원래 task와 반영한 fix 커밋이다.']
    manifest = {}
    for s in selected:
        basis, reason = why[(chapter,s['task'])]
        file_names = []
        for line in s['files']:
            status, name = line.split('\t',1)
            file_names.append(name)
            manifest[name] = '새로' if status == 'A' and name not in manifest else ('지움' if status == 'D' else '고침')
        tasks += [f"- [x] {s['task']} {s['title']}", f'  - 근거: {basis}', '  - 파일: ' + ', '.join(file_names), f"  - 실행하면: captures/shots.json의 {s['task']} 실행 결과가 보인다", f'  - Why: {reason}']
        summary += ['',f"## {s['task']} {s['title']}", f"커밋: {s['commit'][:7]}", f"Source: {s['source']}", '', '| 파일 | 변경 |', '|---|---|']
        for line in s['files']:
            status, name = line.split('\t',1)
            summary.append(f"| {name} | {dict(A='새로',D='지움',M='고침').get(status,status)} |")
        summary += ['', '```diff', git('show', '--format=', '--no-ext-diff', s['commit'], '--', 'ddd'), '```']
    tasks += ['- [x] 수렴 점검', '- [x] 캡처', '- [x] 도식']
    task, title, old, fix = fixes[chapter]
    tasks += [f'- [x] {task} {title}', f'  - 근거: 고침 {old}', '  - 파일: plan.md의 현재 수업 파일과 수정 범위', '  - 실행하면: 고친 코드가 원래 따라치기 단계에 반영된다', '  - Why: 승인한 단순한 수업 흐름과 코드, 자료를 일치시킨다']
    summary += ['',f'## {task} {title}',f'커밋: {fix}', '- 위 task에 고침을 녹였다. 학생에게 별도 수정 단계로 보이지 않는다', '- done 태그는 옮기지 않고 fixes patch로 수정된 장 끝 코드를 재현한다']
    write(P/f'chapters/{chapter}/tasks.md', '\n'.join(tasks))
    write(P/f'chapters/{chapter}/summary-code.md', '\n'.join(summary))
    # Current stage manifest is separate from the fix's cross-chapter cleanup.
    plan = P/f'chapters/{chapter}/plan.md'
    content = plan.read_text(encoding='utf-8').split('## 현재 따라치기 파일 목록')[0].rstrip() + '\n'
    if chapter == 'optimistic-lock':
        content = content.replace('발행기는 new EventPublisher()로 바로 넘긴다', '상품 저장소를 넘겨 15장의 직접 재고 감소를 유지한다')
    content += '\n## 현재 따라치기 파일 목록\n\n| 파일 | 변경 |\n|---|---|\n' + '\n'.join(f'| {name} | {change} |' for name,change in sorted(manifest.items())) + '\n'
    if chapter == 'bounded-contexts':
        removed = git('diff-tree','--no-commit-id','--name-only','--diff-filter=D','-r','170e451','--','ddd').splitlines()
        content += '\n## 강의 범위 축소로 지우는 파일\n\n'+'\n'.join('- '+n for n in removed)+'\n'
    write(plan, content)

for chapter in ['order-events','bounded-contexts']:
    cap = P/f'chapters/{chapter}/captures'
    import json
    shots = json.loads((cap/'shots.json').read_text(encoding='utf-8'))['shots']
    done = next(s for s in shots if s['task']=='done')
    output = (cap/done['file']).read_text(encoding='utf-8')
    if output.startswith('$ '):
        output = output.split('\n',1)[1]
    output = output.lstrip('\n')
    spec = P/f'chapters/{chapter}/spec.md'
    text = spec.read_text(encoding='utf-8')
    text = re.sub(r'## 실행 결과\n.*?(?=## 흐름)', '', text, flags=re.S)
    text = text.replace('## 흐름', '## 실행 결과\n'+output.rstrip()+'\n\n## 흐름',1)
    write(spec,text)

road = (P/'roadmap.md').read_text(encoding='utf-8').replace('| 수정 중 |','| 완료 |')
write(P/'roadmap.md',road)
discount = P/'chapters/discount/spec.md'
text = discount.read_text(encoding='utf-8')
if '## 14장과 비교' not in text:
    text += '\n## 14장과 비교\n- 할인 정책은 회원과 주문으로 할인 금액을 계산한다\n- 무료 배송 명세는 주문이 조건을 만족하는지 참 거짓으로 답한다\n- 무료 배송 기준은 할인 뒤 결제 금액이다\n'
write(discount,text)
print('현재 task, 파일 목록, 코드 요약, 실행 결과와 완료 상태 저장')
