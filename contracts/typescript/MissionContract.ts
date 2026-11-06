/**
 * Verifiable Data Contract: MissionContract (TypeScript)
 * Mirrors Kotlin MissionContract schema for cross-platform interop.
 */
export interface ContractPayload {
  readonly items: string[];
  readonly metric_score: number;
  readonly checksum_sha256: string;
}

export interface MissionContract {
  readonly contract_id: string;
  readonly title: string;
  readonly version: string;
  readonly status: "QUEUED" | "RUNNING" | "COMPLETED" | "VERIFIED" | "FAILED";
  readonly verified: boolean;
  readonly payload: ContractPayload;
}

export function validateMissionContract(data: unknown): data is MissionContract {
  if (!data || typeof data !== "object") return false;
  const contract = data as Record<string, unknown>;
  return (
    typeof contract.contract_id === "string" &&
    typeof contract.title === "string" &&
    typeof contract.version === "string" &&
    typeof contract.status === "string" &&
    typeof contract.verified === "boolean" &&
    contract.payload !== null &&
    typeof contract.payload === "object"
  );
}
