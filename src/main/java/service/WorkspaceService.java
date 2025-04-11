package service;

import dto.Workspace.WorkspaceRequestDTO;
import dto.Workspace.WorkspaceResponseDTO;
import model.Workspace;
import repository.WorkspaceRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class WorkspaceService {

    private final WorkspaceRepository repository = new WorkspaceRepository();

    public void createWorkspace(WorkspaceRequestDTO dto, int companyId) {
        Workspace workspace = new Workspace(
                0, // id
                companyId,
                dto.getName(),
                dto.getCapacity(),
                dto.getDescription(),
                new Timestamp(System.currentTimeMillis())
        );
        repository.create(workspace);
    }

    public List<WorkspaceResponseDTO> getAllWorkspacesForCompany(int companyId) {
        List<Workspace> workspaces = repository.findAllByCompanyId(companyId);
        return workspaces.stream()
                .map(ws -> new WorkspaceResponseDTO(
                        ws.getId(),
                        ws.getName(),
                        ws.getCapacity(),
                        ws.getDescription()
                ))
                .collect(Collectors.toList());
    }

    public void deleteWorkspace(int workspaceId) {
        repository.deleteById(workspaceId);
    }

    public List<WorkspaceResponseDTO> searchWorkspaces(String keyword, int companyId) {
        List<Workspace> filtered = repository.searchByName(companyId, keyword);
        return filtered.stream()
                .map(ws -> new WorkspaceResponseDTO(
                        ws.getId(),
                        ws.getName(),
                        ws.getCapacity(),
                        ws.getDescription()
                ))
                .collect(Collectors.toList());
    }
    public void updateWorkspace(int workspaceId, WorkspaceRequestDTO dto) {
        Workspace existing = repository.findById(workspaceId);
        if (existing != null) {
            existing.setName(dto.getName());
            existing.setCapacity(dto.getCapacity());
            existing.setDescription(dto.getDescription());
            repository.update(existing);
        }
    }
    public List<WorkspaceResponseDTO> getAllWorkspacesForLoggedInCompany() {
        int companyId = SessionManager.getInstance().getLoggedInCompanyId();
        return getAllWorkspacesForCompany(companyId);
    }
}
