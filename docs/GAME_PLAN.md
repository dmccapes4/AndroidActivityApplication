# Clinical Dashboard – Project Game Plan

**Project Name**: Clinical Reasoning Sandbox  
**Goal**: Build a three-Activity Android app demonstrating a graph-based Electronic Health Record (EHR) system with modern Android architecture.

---

## Vision

Create a realistic clinical workflow application consisting of three interconnected activities:

- **PatientActivity** → Patient-facing clinical dashboard
- **DoctorActivity** → Doctor view with record entry
- **GraphActivity** → AI-powered graph analysis and visualization

The app uses a **graph-based data model** stored in Room to simulate real Electronic Health Records.

---

## Core Features

### 1. PatientActivity (Patient Dashboard)
- Loads patient's clinical graph from Room
- Searchable list of clinical nodes (Diagnosis, Medication, Lab Results, Notes, etc.)
- Nodes displayed as rich Material 3 Cards
- Clean, modern UI with good state management

### 2. DoctorActivity (Doctor Dashboard)
- List of patients to select from
- Opens selected patient's dashboard
- Form to add new clinical records (Diagnosis, Medication, Lab, Note)
- Records saved to Room and immediately visible in PatientActivity

### 3. GraphActivity (Clinical Graph Analysis)
- Select a patient
- "Run Analysis" button that calls Python functions
- Displays generated visualizations (PNG images)
- Ability to download images to device

---

## Technical Architecture

### Data Model (Room)
- `Patient`
- `ClinicalNode` (type, title, details, timestamp)
- `ClinicalEdge` (relationships between nodes)
- `Record` (audit log of doctor entries)

### UI / Architecture
- **Single Activity + Jetpack Navigation** (recommended)
- **Jetpack Compose** for all screens
- **ViewModel + StateFlow** for state management
- **Room** with Flow / LiveData observers
- **Repository pattern**

### Python Integration (GraphActivity)
- Use **Chaquopy** or a local HTTP server
- Run graph algorithms (centrality, clustering, risk scoring)
- Generate PNG visualizations
- Return results to Compose UI

---

## Development Phases

### Phase 1: Foundation
- Set up Room database + entities
- Create PatientActivity with mock data
- Implement search and card display

### Phase 2: Doctor Workflow
- Build DoctorActivity
- Add record creation form
- Real-time sync between Doctor and Patient views

### Phase 3: Graph Analysis
- Build GraphActivity
- Integrate Python backend
- Generate and display visualizations
- Add download functionality

### Phase 4: Polish & Interview Ready
- Add proper navigation (NavGraph)
- Improve UI/UX (animations, themes, error states)
- Add tests (JUnit + Compose UI tests)
- Document architecture decisions

---

## Key Learning Objectives (Interview Value)

- Room database with relationships
- Graph data modeling
- Modern Compose architecture
- State management with ViewModel + StateFlow
- Python interop (Chaquopy)
- Real-world clinical workflow simulation
- Clean Architecture / Repository pattern

---

## Next Immediate Steps (Recommended)

1. Create Room database schema + basic entities
2. Build PatientActivity with Room + search
3. Implement DoctorActivity + record entry
4. Add GraphActivity stub

---

**Status**: In Progress  
**Target**: Strong portfolio piece + deep interview discussion topic

---
