# 📁 .github/ISSUE_TEMPLATE/bug_report.md

name: 🐛 버그 제보
description: 잘못된 동작, 예외 등 문제 상황 제보
title: "[BUG] "
labels: [bug]
assignees: ''

body:
- type: markdown
  attributes:
  value: |
  아래 항목들을 모두 채워주세요 🙏

- type: input
  id: summary
  attributes:
  label: 문제 요약
  placeholder: 간단하게 설명해주세요
  validations:
  required: true

- type: textarea
  id: steps
  attributes:
  label: 재현 방법
  placeholder: 어떻게 하면 이 문제가 발생하나요?
  validations:
  required: true

- type: textarea
  id: expected
  attributes:
  label: 기대한 동작
  validations:
  required: false
