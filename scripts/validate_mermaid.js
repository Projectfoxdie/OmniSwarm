#!/usr/bin/env node
/**
 * Autonomous Mermaid Architecture Diagram Syntax & Consistency Validator
 */

const fs = require('fs');

function validateMermaidSequenceDiagram(dsl) {
  const errors = [];
  const warnings = [];
  const lines = dsl.split('\n').map(l => l.trim()).filter(l => l.length > 0 && !l.startsWith('%%'));

  if (lines.length === 0) {
    return { valid: false, errors: ['Empty Mermaid diagram definition'] };
  }

  // Check header
  const firstLine = lines[0];
  if (!firstLine.startsWith('sequenceDiagram')) {
    errors.push(`First directive must be 'sequenceDiagram', found '${firstLine}'`);
  }

  const participants = new Set();
  const validConnectors = ['->>', '-->>', '->', '-->', '-x', '--x', '-)', '--)'];
  let messageCount = 0;

  for (let i = 1; i < lines.length; i++) {
    const line = lines[i];

    if (line.startsWith('autonumber') || line.startsWith('title') || line.startsWith('box') || line === 'end') {
      continue;
    }

    if (line.startsWith('participant ') || line.startsWith('actor ')) {
      const parts = line.split(/\s+/);
      if (parts.length >= 2) {
        participants.add(parts[1]);
      }
      continue;
    }

    // Check message arrow
    const hasConnector = validConnectors.some(c => line.includes(c));
    if (hasConnector) {
      messageCount++;
      const parts = line.split(/->>|-->>|->|-->/);
      if (parts.length >= 2) {
        const sender = parts[0].trim().split(/\s+/)[0];
        participants.add(sender);
      }
    } else if (line.startsWith('Note ') || line.startsWith('rect ') || line.startsWith('alt ') || line.startsWith('opt ') || line.startsWith('loop ')) {
      // Valid structural annotations
      continue;
    } else {
      errors.push(`Line ${i + 1}: Unrecognized Mermaid sequence syntax '${line}'`);
    }
  }

  // Check 4-director architecture consistency
  const requiredRoles = ['SupremeOrchestrator', 'StrategyDirector', 'OperationsDirector', 'QualityDirector'];
  const missingRoles = requiredRoles.filter(r => ![...participants].some(p => p.toLowerCase().includes(r.toLowerCase())));

  if (missingRoles.length > 0) {
    warnings.push(`Missing architectural roles in diagram: ${missingRoles.join(', ')}`);
  }

  return {
    valid: errors.length === 0,
    participantCount: participants.size,
    participants: [...participants],
    messageCount,
    errors,
    warnings
  };
}

// Self-test when run directly
if (require.main === module) {
  const sampleValidDiagram = `
sequenceDiagram
    autonumber
    participant Client as Operator
    participant SO as SupremeOrchestrator
    participant SD as StrategyDirector
    participant OP as OperationsDirector
    participant QD as QualityDirector

    Client->>SO: Deploy mission specification
    SO->>SD: Allocate domain milestone & budgets
    SD->>OP: Dispatch tool execution contracts
    OP->>QD: Submit output artifacts & checksums
    QD-->>SO: Independent verification certification
    SO-->>Client: Final verified deliverables
  `;

  const sampleInvalidDiagram = `
invalidDiagramType
    foo -> bar
    bad syntax ???
  `;

  console.log("=== RUNNING MERMAID VALIDATOR SELF-TEST ===");
  const validRes = validateMermaidSequenceDiagram(sampleValidDiagram);
  console.log("Valid Diagram Test:", validRes.valid ? "PASSED" : "FAILED", `(${validRes.messageCount} messages, ${validRes.participantCount} participants)`);

  const invalidRes = validateMermaidSequenceDiagram(sampleInvalidDiagram);
  console.log("Invalid Diagram Test (Negative):", !invalidRes.valid ? "PASSED (Rejected as expected)" : "FAILED");
  console.log("Detected errors:", invalidRes.errors);

  if (!validRes.valid || invalidRes.valid) {
    process.exit(1);
  }
  process.exit(0);
}

module.exports = { validateMermaidSequenceDiagram };
