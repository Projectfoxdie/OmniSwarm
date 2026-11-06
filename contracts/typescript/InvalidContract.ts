// Controlled negative test fixture: deliberate TypeScript type violations
import { MissionContract } from "./MissionContract";

// Deliberately violating type contract: missing required payload, invalid status type, metric_score as string
export const brokenContract: MissionContract = {
  contract_id: 12345, // Error: Type 'number' is not assignable to type 'string'
  title: null, // Error: Type 'null' is not assignable to type 'string'
  version: "1.0.0",
  status: "INVALID_STATUS", // Error: Type '"INVALID_STATUS"' is not assignable to status union
  verified: "yes", // Error: Type 'string' is not assignable to type 'boolean'
  payload: {
    items: "not_an_array", // Error: Type 'string' is not assignable to type 'string[]'
    metric_score: "high", // Error: Type 'string' is not assignable to type 'number'
    checksum_sha256: 999 // Error: Type 'number' is not assignable to type 'string'
  }
};
