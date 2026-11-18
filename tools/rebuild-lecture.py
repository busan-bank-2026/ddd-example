"""Rebuild only the approved teaching steps in an isolated worktree."""
import json
import pathlib
import shutil
import subprocess
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]
WORK = pathlib.Path('C:/Users/ssarm/AppData/Local/Temp/ddd-lecture-simplify-20261119')
REPORT = pathlib.Path('C:/Users/ssarm/AppData/Local/Temp/ddd-lecture-simplify-report.json')


def git(*args, cwd=ROOT):
    return subprocess.check_output(['git', *args], cwd=cwd).decode('utf-8').strip()


def snapshot(commit):
    files = git('ls-tree', '-r', '--name-only', commit, '--', 'ddd').splitlines()
    return {f: subprocess.check_output(['git', 'show', f'{commit}:{f}'], cwd=ROOT).decode('utf-8') for f in files}


def simple_shipping(files):
    for name in list(files):
        if name.endswith(('/OrderSpecification.java', '/FreeShippingPriceSpecification.java', '/FreeShippingAreaSpecification.java')):
            del files[name]
    files['ddd/src/ddd/order/domain/FreeShippingSpecification.java'] = '''package ddd.order.domain;

public class FreeShippingSpecification {

    public boolean isSatisfiedBy(Order order) {
        return order.getPaymentPrice() >= 50000
                && !order.getAddress().value().startsWith("제주");
    }
}
'''
    text = files['ddd/glossary.md']
    text = '\n'.join(line for line in text.splitlines() if not any(x in line for x in ['| 주문 명세 |', '| 무료 배송 금액 명세 |', '| 무료 배송 지역 명세 |'])) + '\n'
    lines = text.splitlines()
    for i, line in enumerate(lines):
        if line.startswith('| 무료 배송 명세 |'):
            lines[i] = '| 무료 배송 명세 | FreeShippingSpecification | 결제 금액이 50000원 이상이고 주소가 제주로 시작하지 않는지 참 거짓으로 답한다 |'
    files['ddd/glossary.md'] = '\n'.join(lines) + '\n'
    return files


def no_events(files, decrease=True):
    simple_shipping(files)
    for name in list(files):
        if name.endswith(('/OrderPlaced.java', '/EventHandler.java', '/EventPublisher.java', '/DecreaseStockHandler.java')):
            del files[name]
    name = 'ddd/src/ddd/order/domain/Order.java'
    text = files[name]
    text = '\n'.join(line for line in text.splitlines() if not any(x in line for x in ['private List<Object> events;', 'this.events =', 'events.add('])) + '\n'
    text = text.replace('    public List<Object> getEvents() {\n        return List.copyOf(events);\n    }\n\n', '')
    files[name] = text
    service_name = 'ddd/src/ddd/order/application/OrderService.java'
    text = files[service_name]
    if decrease:
        product_package = 'ddd.catalog.domain' if 'ddd/src/ddd/catalog/domain/Product.java' in files else 'ddd.order.domain'
        text = text.replace('import ddd.order.domain.Address;', f'import {product_package}.Product;\nimport {product_package}.ProductRepository;\nimport ddd.order.domain.Address;')
        text = text.replace('EventPublisher eventPublisher', 'ProductRepository productRepository')
        text = text.replace('this.eventPublisher = eventPublisher;', 'this.productRepository = productRepository;')
        text = text.replace('eventPublisher.publish(order.getEvents());', 'for (OrderItem item : order.getItems()) {\n            Product product = productRepository.findById(item.getProductId()).orElseThrow();\n            product.decreaseStock(item.getQuantity());\n            productRepository.save(product);\n        }')
    files[service_name] = text
    name = 'ddd/src/ddd/Main.java'
    text = files[name]
    text = '\n'.join(line for line in text.splitlines() if not any(x in line for x in ['import ddd.order.application.EventPublisher;', 'import ddd.order.application.DecreaseStockHandler;', 'import ddd.catalog.application.DecreaseStockHandler;', 'EventPublisher eventPublisher =', 'eventPublisher.subscribe(', 'eventPublisher.processEvents();'])) + '\n'
    text = text.replace(', eventPublisher)', ', productRepository)').replace(', new EventPublisher())', ', productRepository)')
    text = text.replace('[주문이 주문 접수됨 이벤트 남기기]', '[주문과 재고 확인하기]')
    text = text.replace('"번 주문, 이벤트 " + order.getEvents().size() + "개"', '"번 주문, " + order.getStatus().label()')
    start = text.find('        System.out.println();\n        System.out.println("[이벤트를 처리해 재고 줄이기]");')
    if start >= 0:
        text = text[:start] + '    }\n}\n'
    files[name] = text
    text = files['ddd/glossary.md']
    event_terms = ['| 주문 접수됨 |', '| 이벤트 발행기 |', '| 이벤트 처리기 |', '| 재고 줄이기 처리기 |']
    lines = [line for line in text.splitlines() if not any(x in line for x in event_terms)]
    for i, line in enumerate(lines):
        if line.startswith('| 재고 |'):
            lines[i] = '| 재고 | stock | 상품이 지금 몇 개 남았는지' + ('. 주문하기가 끝나기 전에 주문 수량만큼 줄어든다 |' if decrease else ' |')
    files['ddd/glossary.md'] = '\n'.join(lines) + '\n'
    if 'ddd/context-map.md' in files:
        text = files['ddd/context-map.md']
        text = '\n'.join(line for line in text.splitlines() if not line.startswith('| 상품 | 주문 |')) + '\n'
        text = text.replace('- 컨텍스트 사이를 정해진 창구로 잇는 일은 뒤 장에서 한다', '- 같은 프로그램 안에서 메서드를 호출해 협력한다')
        text += '\n- 주문 응용 서비스는 상품 저장소에서 상품을 찾아 재고를 줄인다\n'
        files['ddd/context-map.md'] = text
    return files


def stock_main(files):
    text = files['ddd/src/ddd/Main.java']
    text = text.replace('[주문과 재고 확인하기]', '[주문 저장 후 재고 줄이기]')
    files['ddd/src/ddd/Main.java'] = text
    return files


def write_state(files):
    target = WORK / 'ddd'
    resolved = target.resolve()
    assert resolved.parent == WORK.resolve() and WORK.resolve().is_relative_to(pathlib.Path('C:/Users/ssarm/AppData/Local/Temp').resolve())
    for f in target.rglob('*'):
        if f.is_file() and not str(f.relative_to(WORK)).replace('\\', '/').startswith('ddd/bin/'):
            f.unlink()
    for name, text in files.items():
        path = WORK / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(text, encoding='utf-8', newline='\n')


def check():
    sources = [str(p.relative_to(WORK)) for p in (WORK / 'ddd/src').rglob('*.java')]
    subprocess.run(['javac', '-encoding', 'UTF-8', '-d', str(WORK / 'compile-output'), *sources], cwd=WORK, check=True, capture_output=True)
    return subprocess.check_output(['java', '-Dfile.encoding=UTF-8', '-cp', str(WORK / 'compile-output'), 'ddd.Main'], cwd=WORK).decode('utf-8')


def commit_stage(files, chapter, task, title, sources):
    write_state(files)
    output = check()
    git('-c', 'core.autocrlf=false', 'add', '--', 'ddd', cwd=WORK)
    msg = f'[{chapter}] {title}\n\nChapter: {chapter}\nTask: {task}\nSource: {sources}\n'
    subprocess.run(['git', 'commit', '-m', msg], cwd=WORK, check=True, capture_output=True)
    result = {'chapter': chapter, 'task': task, 'title': title, 'commit': git('rev-parse', 'HEAD', cwd=WORK), 'source': sources, 'output': output, 'compiled': True}
    print(json.dumps(result, ensure_ascii=False), flush=True)
    return result


def final_snapshot():
    files = {name: text for name, text in snapshot('61b08b1').items() if not name.startswith('ddd/src/') and not name.endswith('.md')}
    for path in (ROOT / 'ddd/src').rglob('*.java'):
        files[path.relative_to(ROOT).as_posix()] = path.read_text(encoding='utf-8')
    for path in (ROOT / 'ddd').glob('*.md'):
        files[path.relative_to(ROOT).as_posix()] = path.read_text(encoding='utf-8')
    return files


def finish(fix):
    rows = json.loads(REPORT.read_text(encoding='utf-8'))
    final = final_snapshot()
    files = dict(final)
    for name in list(files):
        if name.endswith(('/OrderPlaced.java', '/EventPublisher.java', '/DecreaseStockHandler.java')):
            del files[name]
    name = 'ddd/src/ddd/order/application/OrderService.java'
    text = files[name].replace('import ddd.order.domain.OrderPlaced;\n', '')
    text = text.replace('import ddd.member.domain.Member;', 'import ddd.catalog.application.ProductService;\nimport ddd.member.domain.Member;')
    text = text.replace('EventPublisher eventPublisher', 'ProductService productService').replace('this.eventPublisher = eventPublisher;', 'this.productService = productService;')
    text = text.replace('eventPublisher.publish(new OrderPlaced(order.getId(), order.getItems()));', 'for (OrderItem item : order.getItems()) {\n            productService.decreaseStock(item.getProductId(), item.getQuantity());\n        }')
    files[name] = text
    name = 'ddd/src/ddd/Main.java'
    text = files[name]
    text = '\n'.join(line for line in text.splitlines() if not any(x in line for x in ['import ddd.catalog.application.DecreaseStockHandler;', 'import ddd.order.application.EventPublisher;', 'EventPublisher eventPublisher ='])) + '\n'
    text = text.replace(', eventPublisher)', ', productService)').replace('[주문 접수 이벤트로 재고 줄이기]', '[상품 서비스로 재고 줄이기]')
    files[name] = text
    text = files['ddd/glossary.md']
    files['ddd/glossary.md'] = '\n'.join(line for line in text.splitlines() if not any(x in line for x in ['| 주문 접수됨 |', '| 이벤트 발행기 |', '| 이벤트 처리기 |', '| 재고 줄이기 처리기 |'])) + '\n'
    name = 'ddd/where-rules-live.md'
    files[name] = '\n'.join(line for line in files[name].splitlines() if '이벤트' not in line and 'Event' not in line) + '\n'
    files['ddd/context-map.md'] = '''# 컨텍스트 맵

## 하위 도메인과 컨텍스트

| 업무 | 종류 | 책임 | 묶음 |
|---|---|---|---|
| 주문 | 핵심 | 주문, 결제, 배송, 취소 규칙 | ddd.order |
| 상품 | 지원 | 상품 가격과 재고 | ddd.catalog |
| 회원 | 일반 | 회원 이름과 등급 | ddd.member |

- 하위 도메인은 업무를 나눈 것이다
- 바운디드 컨텍스트는 한 모델과 업무 말이 통하는 경계다
- 컨텍스트 안에는 domain, application, infrastructure 계층을 둔다
- 주문 항목은 주문 당시의 상품 이름과 가격을 갖는다
- 상품은 현재 가격과 재고를 갖는다

## 서비스로 협력하기

- MemberService는 회원 조회와 목록 조회를 맡는다
- ProductService는 상품 조회와 목록 조회, 재고 감소를 맡는다
- OrderService는 주문을 저장한 뒤 ProductService에 재고 감소를 요청한다
- 주문 항목 팩토리는 상품 저장소에서 상품 이름과 가격을 읽는다
- 각 서비스는 같은 프로그램 안에서 일반 메서드 호출로 협력한다
'''
    rows.append(commit_stage(files, 'bounded-contexts', 'T7', '회원과 상품 서비스 만들기', fix))
    rows.append(commit_stage(final, 'bounded-contexts', 'T8', '주문 접수 이벤트로 재고 감소 연결하기', fix))
    REPORT.write_text(json.dumps(rows, ensure_ascii=False, indent=2), encoding='utf-8')
    git('branch', '-f', 'lecture', rows[-1]['commit'])
    print('LECTURE_READY ' + rows[-1]['commit'], flush=True)


def clean_generated():
    for folder in [WORK / 'compile-output', WORK / 'ddd/bin']:
        assert folder.resolve().is_relative_to(WORK.resolve())
        if folder.exists():
            shutil.rmtree(folder)


def resources():
    rows = [row for row in json.loads(REPORT.read_text(encoding='utf-8')) if not (row['chapter'] == 'free-shipping' and row['task'] == 'T1')]
    prefix = {'chapter': 'free-shipping', 'task': 'T1', 'title': '무료 배송 명세로 배송비 정하기', 'commit': '710602e'}
    allrows = [prefix, *rows]
    clean_generated()
    groups = {}
    for row in allrows:
        groups.setdefault(row['chapter'], []).append(row)
    base_chapters = {'free-shipping': 'discount', 'order-events': 'free-shipping', 'optimistic-lock': 'order-events', 'bounded-contexts': 'optimistic-lock'}
    flows = {
        'free-shipping': '  A[배송비 묻기] --> B[무료 배송 명세]\n  B --> C{5만원 이상이고 제주 밖}\n  C -->|참| D[배송비 0원]\n  C -->|거짓| E[배송비 3000원]',
        'order-events': '  A[주문 요청] --> B[주문 항목 만들기]\n  B --> C[주문 저장]\n  C --> D[상품 찾기]\n  D --> E[재고 감소]\n  E --> F[상품 저장]\n  F --> G[주문 번호 반환]',
        'optimistic-lock': '  A[같은 주문 두 번 조회] --> B[첫째 배송 후 저장]\n  B --> C[저장된 버전 증가]\n  C --> D[둘째 취소 후 저장]\n  D --> E{버전이 같은가}\n  E -->|다름| F[저장 거절]',
        'bounded-contexts': '  A[주문 요청] --> B[주문 서비스]\n  B --> C[주문 저장]\n  C --> D[주문 접수됨 발행]\n  D --> E[재고 감소 처리기]\n  E --> F[상품 서비스]\n  F --> G[상품 재고 감소]\n  G --> H[주문 번호 반환]',
    }
    for chapter, steps in groups.items():
        cap = ROOT / '.projectcode/chapters' / chapter / 'captures'
        cap.mkdir(parents=True, exist_ok=True)
        end = steps[-1]
        shots = []
        for row in steps:
            shots.append({'file': f"{row['task']}-{row['title']}.txt", 'task': row['task'], 'commit': row['commit'], 'screen': '콘솔 실행', 'see': row['title'] + '. ' + row.get('output', '').strip().split('\n')[-1], 'run': {}})
        shots.append({'file': 'done-주문-장면.txt', 'task': 'done', 'commit': end['commit'], 'screen': '장 끝 콘솔 실행', 'see': '장 끝 실행 결과. ' + end.get('output', '').strip().split('\n')[-1], 'run': {}})
        spec = {'chapter': chapter, 'command': 'javac -encoding UTF-8 -d bin --source-path src src/ddd/Main.java && java -Dfile.encoding=UTF-8 -cp bin ddd.Main', 'cwd': 'ddd', 'shots': shots}
        spec_path = cap / 'shots.json'
        spec_path.write_text(json.dumps(spec, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
        wanted = {'shots.json', 'captures.md', *(s['file'] for s in shots)}
        for f in cap.iterdir():
            if f.is_file() and f.name not in wanted:
                f.unlink()
        for shot in shots:
            git('checkout', '--detach', shot['commit'], cwd=WORK)
            row = next((r for r in steps if r['commit'] == shot['commit']), None)
            if row and 'output' not in row:
                row['output'] = check()
                row['compiled'] = True
                clean_generated()
            subprocess.run(['node', str(ROOT / '.claude/skills/projectcode-capture/capture.mjs'), str(spec_path), '--task', shot['task']], cwd=WORK, check=True, capture_output=True)
            result = (cap / shot['file']).read_text(encoding='utf-8')
            assert '(끝난 코드 ' not in result, result
            if row and 'output' in row:
                captured = result.split('\n', 1)[1].strip().replace('\r\n', '\n')
                assert captured == row['output'].strip().replace('\r\n', '\n'), (shot['file'], captured, row['output'])
            clean_generated()
        diagrams = ROOT / '.projectcode/chapters' / chapter / 'diagrams'
        diagrams.mkdir(parents=True, exist_ok=True)
        flow = f'# {chapter} 흐름\n\n```mermaid\nflowchart LR\n{flows[chapter]}\n```\n'
        if chapter == 'bounded-contexts':
            flow += '\n회원 서비스는 회원 조회와 목록 조회를, 상품 서비스는 상품 조회와 목록 조회, 재고 감소를 맡는다.\n발행기부터 처리기까지 같은 스레드에서 즉시 호출한다.\n동기 호출만으로 주문 저장과 상품 저장이 하나의 트랜잭션이 되지는 않는다.\n'
        (diagrams / 'flow.md').write_text(flow, encoding='utf-8')
        previous = base_chapters[chapter]
        base = groups[previous][-1]['commit'] if previous in groups else git('log', '-1', '--format=%H', '--grep=^Chapter: ' + previous + '$', 'lecture')
        tree = subprocess.check_output(['node', str(ROOT / '.claude/skills/projectcode-diagram/tree.mjs'), base], cwd=WORK).decode('utf-8')
        tree = tree.replace(f'비교 기준: {base}.', f'비교 기준: done/{previous}의 수정을 반영한 앞 장 끝. 앞 장 수정 반영 시점: {base}.')
        (diagrams / 'tree.md').write_text(tree, encoding='utf-8')
        print('CAPTURED ' + chapter + ' ' + str(len(shots)), flush=True)
    for chapter, steps in groups.items():
        end = steps[-1]['commit']
        git('checkout', '--detach', 'done/' + chapter, cwd=WORK)
        fixes = ROOT / '.projectcode/chapters' / chapter / 'fixes'
        fixes.mkdir(parents=True, exist_ok=True)
        fixhash = '7574f96' if chapter == 'free-shipping' else '170e451'
        patchfile = fixes / (fixhash + '.patch')
        for old in sorted(fixes.glob('*.patch')):
            if old == patchfile:
                continue
            subprocess.run(['git', 'apply', '--index', str(old)], cwd=WORK, check=True, capture_output=True)
        tree = git('write-tree', cwd=WORK)
        patch = subprocess.check_output(['git', 'diff', '--binary', tree, end, '--', 'ddd'], cwd=WORK)
        patchfile.write_bytes(patch)
        subprocess.run(['git', 'apply', '--index', str(patchfile)], cwd=WORK, check=True, capture_output=True)
        assert not git('diff', '--cached', end, '--', 'ddd', cwd=WORK), chapter
        output = check()
        assert output.strip() == steps[-1]['output'].strip()
        clean_generated()
        git('restore', '--source=HEAD', '--staged', '--worktree', '--', '.', cwd=WORK)
        print('PATCH_VERIFIED ' + chapter + ' ' + patchfile.name, flush=True)
    git('checkout', '--detach', 'lecture', cwd=WORK)
    REPORT.write_text(json.dumps(allrows, ensure_ascii=False, indent=2), encoding='utf-8')
    print('RESOURCES_READY', flush=True)


def main():
    if len(sys.argv) > 1 and sys.argv[1] == '--trees':
        rows = json.loads(REPORT.read_text(encoding='utf-8'))
        rows = [r for r in rows if not (r['chapter'] == 'free-shipping' and r['task'] == 'T1')]
        git('checkout', '--detach', '710602e', cwd=WORK)
        prefix = {'chapter': 'free-shipping', 'task': 'T1', 'title': '무료 배송 명세로 배송비 정하기', 'commit': git('rev-parse', 'HEAD', cwd=WORK), 'source': '15ffe4f a312669', 'output': check(), 'compiled': True}
        clean_generated()
        groups = {}
        for row in [prefix, *rows]:
            groups.setdefault(row['chapter'], []).append(row)
        previous_chapters = {'free-shipping': 'discount', 'order-events': 'free-shipping', 'optimistic-lock': 'order-events', 'bounded-contexts': 'optimistic-lock'}
        for chapter, steps in groups.items():
            git('checkout', '--detach', steps[-1]['commit'], cwd=WORK)
            previous = previous_chapters[chapter]
            base = groups[previous][-1]['commit'] if previous in groups else git('log', '-1', '--format=%H', '--grep=^Chapter: ' + previous + '$', 'lecture')
            tree = subprocess.check_output(['node', str(ROOT / '.claude/skills/projectcode-diagram/tree.mjs'), base], cwd=WORK).decode('utf-8')
            tree = tree.replace(f'비교 기준: {base}.', f'비교 기준: done/{previous}의 수정을 반영한 앞 장 끝. 앞 장 수정 반영 시점: {base}.')
            (ROOT / '.projectcode/chapters' / chapter / 'diagrams/tree.md').write_text(tree, encoding='utf-8')
        REPORT.write_text(json.dumps([prefix, *rows], ensure_ascii=False, indent=2), encoding='utf-8')
        git('checkout', '--detach', 'lecture', cwd=WORK)
        print('TREES_AND_REPORT_READY', flush=True)
        return
    if len(sys.argv) > 1 and sys.argv[1] == '--finish':
        finish(sys.argv[2])
        return
    if len(sys.argv) > 1 and sys.argv[1] == '--resources':
        resources()
        return
    rows = []
    fix = sys.argv[1] if len(sys.argv) > 1 else ''
    source = lambda base: (base + ' ' + ('7574f96' if base.startswith('7834719') else fix)).strip()
    git('checkout', '--detach', '710602e', cwd=WORK)
    rows.append(commit_stage(simple_shipping(snapshot('ee660c2')), 'free-shipping', 'T2', '무료 배송 명세에 제주 조건 더하기', source('7834719 a312669')))
    files = no_events(snapshot('394ed04'), decrease=False)
    rows.append(commit_stage(files, 'order-events', 'T1', '상품에 재고 더하기', source('8148820 a312669')))
    files = stock_main(no_events(snapshot('5f7a0a3')))
    rows.append(commit_stage(files, 'order-events', 'T2', '주문 저장 뒤 상품 재고 바로 줄이기', source('388255f 3d27ec7 a312669')))
    for h, task, title, src in [('65d5f9e', 'T1', '같은 주문을 두 번 꺼내 덮어쓰기', '001c774'), ('c43e161', 'T2', '저장할 때마다 주문 버전 올리기', '1c6f7bb'), ('eeeec9d', 'T3', '버전이 다르면 저장 거절하기', '2a1cb4e')]:
        rows.append(commit_stage(no_events(snapshot(h)), 'optimistic-lock', task, title, source(src + ' a312669')))
    for h, task, title, src in [('5924f96', 'T1', '하위 도메인과 컨텍스트 정하기', '8a46784'), ('26d387d', 'T3', '회원 컨텍스트 만들기', 'e19673d'), ('6648b3e', 'T4', '상품 컨텍스트 만들기', '27bbabf'), ('61b08b1', 'T5', '컨텍스트 사이에 쓰는 곳 적기', 'ced0444')]:
        rows.append(commit_stage(no_events(snapshot(h)), 'bounded-contexts', task, title, source(src + ' a312669')))
    REPORT.write_text(json.dumps(rows, ensure_ascii=False, indent=2), encoding='utf-8')
    print('BASE_READY ' + str(REPORT), flush=True)
    if '--all' in sys.argv:
        finish(fix)
        resources()


if __name__ == '__main__':
    main()
