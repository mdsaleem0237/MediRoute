package project;

import java.util.Scanner;

public class MediRoute {

    // =========================================================================
    // UI REFRESH HELPER (Clean single break - no huge gaps)
    // =========================================================================
    public static void clearScreen(Scanner sc) {
        System.out.print("\nPress [ENTER] to return to Dashboard...");
        sc.nextLine();
        System.out.println();
    }

    // =========================================================================
    // MAIN DASHBOARD
    // =========================================================================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Facilities Data
        String[] facilities = {
            "Central Warehouse (WH-0)", 
            "Regional Depot A (WH-1)", 
            "City General Hospital (H-2)", 
            "Trauma ICU Center (H-3)",
            "Apollo Care Clinic (H-4)"
        };
        int facilityCount = 5;

        // Inventory
        MedicineBatch[] inventory = new MedicineBatch[100];
        int batchCount = 0;
        inventory[batchCount++] = new MedicineBatch("MED-PAR-101", "Paracetamol", 4500, "2027-08", 0);
        inventory[batchCount++] = new MedicineBatch("MED-REM-202", "Remdesivir", 300, "2026-11", 0);
        inventory[batchCount++] = new MedicineBatch("MED-INS-303", "Insulin", 150, "2026-06", 1); // Expired
        inventory[batchCount++] = new MedicineBatch("MED-AMX-404", "Amoxicillin", 1200, "2027-03", 1);

        // Initial scan: isolate expired medicines
        String currentThreshold = "2026-10";
        for (int i = 0; i < batchCount; i++) {
            if (inventory[i].expiryDate.compareTo(currentThreshold) <= 0) {
                inventory[i].quarantined = true;
            }
        }

        // Hospital Requests
        HospitalRequest[] requests = new HospitalRequest[50];
        int requestCount = 0;
        requests[requestCount++] = new HospitalRequest(2, "Paracetamol", 2000);
        requests[requestCount++] = new HospitalRequest(2, "Remdesivir", 250);
        requests[requestCount++] = new HospitalRequest(3, "Insulin", 500);

        // Shipments
        ShipmentRecord[] shipments = new ShipmentRecord[100];
        int shipmentCount = 0;
        shipments[shipmentCount++] = new ShipmentRecord("SHP-9001", "Central Warehouse (WH-0)", "City General Hospital (H-2)", "Paracetamol", 1000, "DELIVERED");
        shipments[shipmentCount++] = new ShipmentRecord("SHP-9002", "Regional Depot A (WH-1)", "Trauma ICU Center (H-3)", "Amoxicillin", 300, "IN-TRANSIT");

        // Network Flow
        RouteOptimizer flowNet = new RouteOptimizer();
        flowNet.setCapacity(0, 1, 1500);
        flowNet.setCapacity(0, 2, 800);
        flowNet.setCapacity(1, 2, 400);
        flowNet.setCapacity(1, 3, 700);
        flowNet.setCapacity(2, 3, 300);
        flowNet.setCapacity(1, 4, 350);

        // Corridors Setup: Hub-and-Spoke structure
        int[][] corridors = new int[20][2];
        int corridorCount = 0;
        corridors[corridorCount++] = new int[]{0, 2}; // WH-0 to H-2
        corridors[corridorCount++] = new int[]{0, 3}; // WH-0 to H-3
        corridors[corridorCount++] = new int[]{0, 4}; // WH-0 to H-4
        corridors[corridorCount++] = new int[]{1, 2}; // WH-1 to H-2
        corridors[corridorCount++] = new int[]{1, 3}; // WH-1 to H-3

        String[] catalog = {"Paracetamol", "Remdesivir", "Insulin", "Amoxicillin", "Azithromycin", "Dexamethasone"};

        while (true) {
            System.out.println("===============================================================================");
            System.out.println("              MEDIROUTE : MEDICAL SUPPLY & LOGISTICS DASHBOARD                ");
            System.out.println("===============================================================================");
            System.out.println(" 1. Warehouse Stock Status (Approved & Safe Inventory)");
            System.out.println(" 2. Hospital Monthly Requests & Direct Allocation");
            System.out.println(" 3. Expiry Date & Quarantine Management (Isolation & Disposal)");
            System.out.println(" 4. Past Shipment Status & Audit Ledger (Search KMP/Rabin-Karp)");
            System.out.println(" 5. Emergency Supply Flow Optimization (Edmonds-Karp)");
            System.out.println(" 6. Critical Transport Checkpoint Selection (2-Approx Vertex Cover)");
            System.out.println(" 7. Medicine Search, Typo Correction & Availability Check");
            System.out.println(" 8. Exit System");
            System.out.println("-------------------------------------------------------------------------------");
            System.out.print("Select Menu Option [1-8]: ");

            if (!sc.hasNextInt()) break;
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                // 1. WAREHOUSE INVENTORY STATUS
                case 1 -> {
                    System.out.println("\n--- Current Available Warehouse Stock (Approved & Safe Only) ---");
                    System.out.printf("%-14s | %-15s | %-8s | %-10s | %-26s | %s\n",
                            "BATCH CODE", "DRUG NAME", "QTY", "EXPIRY", "FACILITY", "STATUS");
                    System.out.println("---------------+-----------------+----------+------------+----------------------------+-----------");

                    int availableBatches = 0;
                    int quarantinedBatches = 0;

                    for (int i = 0; i < batchCount; i++) {
                        MedicineBatch b = inventory[i];
                        if (!b.quarantined) {
                            System.out.printf("%-14s | %-15s | %-8d | %-10s | %-26s | AVAILABLE\n",
                                    b.batchCode, b.drugName, b.quantity, b.expiryDate, facilities[b.warehouseId]);
                            availableBatches++;
                        } else {
                            quarantinedBatches++;
                        }
                    }

                    if (availableBatches == 0) System.out.println("  [!] No active safe stock found.");
                    if (quarantinedBatches > 0) {
                        System.out.printf("\n[NOTE] %d expired/quarantined batch(es) hidden from active inventory. Use Option 3 to inspect.\n", quarantinedBatches);
                    }

                    System.out.print("\nAdd a new medicine batch to warehouse? (y/n): ");
                    if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                        System.out.print("Enter Batch Code (e.g., MED-AZI-505): ");
                        String code = sc.nextLine().trim();
                        System.out.print("Enter Medicine Name: ");
                        String med = sc.nextLine().trim();
                        System.out.print("Enter Quantity (units): ");
                        int qty = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Enter Expiry Date (YYYY-MM): ");
                        String exp = sc.nextLine().trim();

                        System.out.println("Select Destination Warehouse:");
                        System.out.println(" 0: Central Warehouse (WH-0)");
                        System.out.println(" 1: Regional Depot A (WH-1)");
                        System.out.print("Choice: ");
                        int whId = sc.nextInt();
                        sc.nextLine();

                        if (whId == 0 || whId == 1) {
                            MedicineBatch newBatch = new MedicineBatch(code, med, qty, exp, whId);
                            if (exp.compareTo(currentThreshold) <= 0) {
                                newBatch.quarantined = true;
                                System.out.println("[WARNING] Batch already expired! Immediately routed to Quarantine.");
                            } else {
                                System.out.println("Successfully added " + qty + " units of " + med + " to " + facilities[whId]);
                            }
                            inventory[batchCount++] = newBatch;
                        }
                    }
                    clearScreen(sc);
                }

                // 2. HOSPITAL DEMANDS & ALLOCATION
                case 2 -> {
                    System.out.println("\n--- Monthly Hospital Requirements & Fulfillment ---");
                    System.out.printf("%-4s | %-26s | %-14s | %-14s | %-14s | %s\n",
                            "REQ#", "HOSPITAL", "MEDICINE", "MONTHLY QUOTA", "FULFILLED", "PENDING");
                    System.out.println("-----+----------------------------+----------------+----------------+----------------+---------");

                    for (int i = 0; i < requestCount; i++) {
                        HospitalRequest r = requests[i];
                        int pending = r.monthlyQuota - r.fulfilledUnits;
                        System.out.printf("%-4d | %-26s | %-14s | %-14d | %-14d | %d units\n",
                                i + 1, facilities[r.hospitalId], r.drugName, r.monthlyQuota, r.fulfilledUnits, pending);
                    }

                    System.out.println("\nSub-Options: 1. Add Request | 2. Fulfill Request | 3. Back");
                    System.out.print("Choose: ");
                    int sub = sc.nextInt();
                    sc.nextLine();

                    if (sub == 1) {
                        System.out.println("Select Hospital ID [2: City General | 3: Trauma ICU | 4: Apollo Clinic]: ");
                        int hId = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Medicine Name: ");
                        String med = sc.nextLine().trim();
                        System.out.print("Monthly Required Units: ");
                        int quota = sc.nextInt();
                        sc.nextLine();

                        if (hId >= 2 && hId < facilityCount) {
                            requests[requestCount++] = new HospitalRequest(hId, med, quota);
                            System.out.println("Hospital demand quota registered.");
                        }
                    } else if (sub == 2) {
                        System.out.print("Enter Request # to fulfill: ");
                        int rIdx = sc.nextInt() - 1;
                        sc.nextLine();

                        if (rIdx >= 0 && rIdx < requestCount) {
                            HospitalRequest req = requests[rIdx];
                            int stillNeeded = req.monthlyQuota - req.fulfilledUnits;

                            int safeStock = 0;
                            for (int i = 0; i < batchCount; i++) {
                                if (!inventory[i].quarantined && inventory[i].drugName.equalsIgnoreCase(req.drugName)) {
                                    safeStock += inventory[i].quantity;
                                }
                            }

                            System.out.printf("Request: %d units of %s. Still needed: %d units.\n", req.monthlyQuota, req.drugName, stillNeeded);
                            System.out.println("Safe/Verified stock available in network: " + safeStock + " units.");

                            if (safeStock == 0) {
                                System.out.println("[BLOCKED] Zero safe stock available. All batches of this drug are expired or unavailable!");
                            } else {
                                System.out.print("Enter amount to dispatch: ");
                                int dispatch = sc.nextInt();
                                sc.nextLine();

                                if (dispatch > 0 && dispatch <= safeStock && dispatch <= stillNeeded) {
                                    int rem = dispatch;
                                    for (int i = 0; i < batchCount && rem > 0; i++) {
                                        if (!inventory[i].quarantined && inventory[i].drugName.equalsIgnoreCase(req.drugName)) {
                                            int take = Math.min(rem, inventory[i].quantity);
                                            inventory[i].quantity -= take;
                                            rem -= take;
                                        }
                                    }
                                    req.fulfilledUnits += dispatch;
                                    shipments[shipmentCount++] = new ShipmentRecord(
                                            "SHP-DISP-" + (1000 + shipmentCount),
                                            "Consolidated Warehouse",
                                            facilities[req.hospitalId],
                                            req.drugName,
                                            dispatch,
                                            "DISPATCHED"
                                    );
                                    System.out.println("Dispatched " + dispatch + " units to " + facilities[req.hospitalId]);
                                } else {
                                    System.out.println("[ERROR] Exceeds safe stock or pending quota.");
                                }
                            }
                        }
                    }
                    clearScreen(sc);
                }

                // 3. EXPIRY & QUARANTINE MANAGER
                case 3 -> {
                    System.out.println("\n--- Medicine Expiry Date & Cold-Chain Quarantine Manager ---");
                    System.out.print("Enter reference threshold date (e.g., 2026-10): ");
                    String inDate = sc.nextLine().trim();
                    if (!inDate.isEmpty()) currentThreshold = inDate;

                    int quarantinedCount = 0;
                    System.out.printf("%-14s | %-14s | %-8s | %-10s | %s\n",
                            "BATCH CODE", "MEDICINE", "QTY", "EXPIRY", "STATUS");
                    System.out.println("---------------+----------------+----------+------------+-----------------------------------");

                    for (int i = 0; i < batchCount; i++) {
                        MedicineBatch b = inventory[i];
                        if (b.expiryDate.compareTo(currentThreshold) <= 0) {
                            b.quarantined = true;
                            quarantinedCount++;
                            System.out.printf("%-14s | %-14s | %-8d | %-10s | [QUARANTINED - EXPIRED]\n",
                                    b.batchCode, b.drugName, b.quantity, b.expiryDate);
                        } else {
                            System.out.printf("%-14s | %-14s | %-8d | %-10s | ACTIVE (Safe for use)\n",
                                    b.batchCode, b.drugName, b.quantity, b.expiryDate);
                        }
                    }

                    if (quarantinedCount > 0) {
                        System.out.print("\nPermanently purge (dispose) all quarantined batches from storage? (y/n): ");
                        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                            int w = 0;
                            for (int r = 0; r < batchCount; r++) {
                                if (!inventory[r].quarantined) inventory[w++] = inventory[r];
                            }
                            System.out.println("Permanently disposed " + (batchCount - w) + " expired batch(es).");
                            batchCount = w;
                        }
                    }
                    clearScreen(sc);
                }

                // 4. PAST SHIPMENTS
                case 4 -> {
                    System.out.println("\n--- Past Shipment Ledger ---");
                    System.out.printf("%-14s | %-24s | %-26s | %-14s | %-6s | %s\n",
                            "SHIPMENT ID", "ORIGIN", "DESTINATION", "MEDICINE", "QTY", "STATUS");
                    System.out.println("---------------+--------------------------+----------------------------+----------------+--------+-----------");

                    StringBuilder ledger = new StringBuilder();
                    for (int i = 0; i < shipmentCount; i++) {
                        ShipmentRecord s = shipments[i];
                        System.out.printf("%-14s | %-24s | %-26s | %-14s | %-6d | %s\n",
                                s.shipmentId, s.fromFacility, s.toHospital, s.drugName, s.units, s.status);
                        ledger.append(s.shipmentId).append(" ").append(s.fromFacility).append(" ")
                              .append(s.toHospital).append(" ").append(s.drugName).append(" | ");
                    }

                    System.out.print("\nSearch ledger (by Code, Drug, or Facility): ");
                    String q = sc.nextLine().trim();
                    if (!q.isEmpty()) {
                        CustomIntList kmp = LogSearcher.searchKMP(ledger.toString(), q);
                        CustomIntList rk = LogSearcher.searchRabinKarp(ledger.toString(), q);
                        System.out.print(" -> KMP Matches at indices       : ");
                        for (int i = 0; i < kmp.size(); i++) System.out.print(kmp.get(i) + " ");
                        System.out.print("\n -> Rabin-Karp Matches at indices: ");
                        for (int i = 0; i < rk.size(); i++) System.out.print(rk.get(i) + " ");
                        System.out.println();
                    }
                    clearScreen(sc);
                }

                // 5. EMERGENCY SUPPLY FLOW
                case 5 -> {
                    System.out.println("\n-------------------------------------------------------------------------------");
                    System.out.println("                 EMERGENCY SUPPLY PIPELINE OPTIMIZATION (EDMONDS-KARP)         ");
                    System.out.println("-------------------------------------------------------------------------------");
                    System.out.println(" [RECEIVING HOSPITALS & DEFICIT STATUS]");

                    int maxDeficit = -1;
                    int suggestedHospital = 2;

                    for (int h = 2; h < facilityCount; h++) {
                        int pending = 0;
                        for (int r = 0; r < requestCount; r++) {
                            if (requests[r].hospitalId == h) {
                                pending += (requests[r].monthlyQuota - requests[r].fulfilledUnits);
                            }
                        }
                        String alert = (pending > 0) ? "[CRITICAL DEFICIT: " + pending + " units]" : "[QUOTA SATISFIED]";
                        System.out.printf("   [%d] %-26s | %s\n", h, facilities[h], alert);
                        if (pending > maxDeficit) {
                            maxDeficit = pending;
                            suggestedHospital = h;
                        }
                    }

                    System.out.printf("\n>>> SYSTEM SUGGESTION: Target Hospital [%d] (%s) has highest unmet demand.\n",
                            suggestedHospital, facilities[suggestedHospital]);

                    System.out.print("\nSelect Supply Source ID [Default: 0]: ");
                    String srcIn = sc.nextLine().trim();
                    int src = srcIn.isEmpty() ? 0 : Integer.parseInt(srcIn);

                    System.out.printf("Select Destination Hospital ID [Default: %d]: ", suggestedHospital);
                    String dstIn = sc.nextLine().trim();
                    int dst = dstIn.isEmpty() ? suggestedHospital : Integer.parseInt(dstIn);

                    int maxDelivery = flowNet.calculateMaxFlow(src, dst, facilityCount);
                    System.out.println("\n=======================================================");
                    System.out.printf(" MAXIMUM DELIVERABLE SUPPLY FLOW: %d units/hr\n", maxDelivery);
                    System.out.println("=======================================================");
                    flowNet.printFlowBreakdown(facilityCount, facilities);
                    clearScreen(sc);
                }

                // 6. CORRIDOR INSPECTIONS (2-APPROX VERTEX COVER)
                case 6 -> {
                    System.out.println("\n--- Strategic Inspection Checkpoint Selection (2-Approx Vertex Cover) ---");
                    System.out.println("Active Medical Transit Corridors to Monitor:");
                    for (int i = 0; i < corridorCount; i++) {
                        System.out.printf("  Corridor %d: [%s] <=====> [%s]\n",
                                i + 1, facilities[corridors[i][0]], facilities[corridors[i][1]]);
                    }

                    boolean[] checks = CorridorMonitor.selectCheckpoints(facilityCount, corridors, corridorCount);
                    System.out.println("\nSelected Inspection Checkpoints:");
                    int total = 0;
                    for (int i = 0; i < facilityCount; i++) {
                        if (checks[i]) {
                            System.out.printf("  [✓] %s\n", facilities[i]);
                            total++;
                        }
                    }
                    System.out.printf("\nOutcome: %d checkpoints selected to cover all %d corridors (Savings: %d checkpoints avoided).\n",
                            total, corridorCount, (facilityCount - total));
                    clearScreen(sc);
                }

                // 7. DRUG SPELL-CHECK & STOCK AVAILABILITY CHECK
                case 7 -> {
                    System.out.println("\n--- Emergency Drug Search & Stock Availability ---");
                    System.out.println("Registered Drugs: Paracetamol, Remdesivir, Insulin, Amoxicillin, Azithromycin, Dexamethasone");
                    System.out.print("Enter drug name query (e.g., 'insulin' or 'Paracetmol'): ");
                    String query = sc.nextLine().trim();

                    if (!query.isEmpty()) {
                        String matchedDrug = null;
                        int minCost = Integer.MAX_VALUE;

                        for (String registered : catalog) {
                            int dist = DrugCorrector.getDistance(registered, query);
                            if (dist < minCost) {
                                minCost = dist;
                                matchedDrug = registered;
                            }
                        }

                        System.out.println("\n----------------- Verification Result -----------------");
                        if (minCost == 0) {
                            System.out.println("Status: Exact Match Confirmed -> \"" + matchedDrug + "\"");
                        } else {
                            System.out.println("Status: Typo Identified! Did you mean -> \"" + matchedDrug + "\" (Edit Distance = " + minCost + ")?");
                        }

                        // Check actual safe warehouse availability
                        int safeStock = 0;
                        int quarantinedStock = 0;
                        for (int i = 0; i < batchCount; i++) {
                            if (inventory[i].drugName.equalsIgnoreCase(matchedDrug)) {
                                if (!inventory[i].quarantined) {
                                    safeStock += inventory[i].quantity;
                                } else {
                                    quarantinedStock += inventory[i].quantity;
                                }
                            }
                        }

                        System.out.println("Warehouse Stock Status:");
                        if (safeStock > 0) {
                            System.out.println(" -> [AVAILABLE]: " + safeStock + " safe units in active stock ready for dispatch.");
                        } else if (quarantinedStock > 0) {
                            System.out.println(" -> [OUT OF STOCK / CRITICAL]: 0 safe units available!");
                            System.out.println("    (" + quarantinedStock + " units of " + matchedDrug + " are present in warehouse but locked in QUARANTINE due to expiry).");
                        } else {
                            System.out.println(" -> [OUT OF STOCK]: 0 units present in network inventory.");
                        }
                        System.out.println("-------------------------------------------------------");
                    }
                    clearScreen(sc);
                }

                case 8 -> {
                    System.out.println("\nShutting down MediRoute System. System secured.");
                    return;
                }

                default -> System.out.println("Invalid selection. Enter 1 to 8.");
            }
        }
    }
}