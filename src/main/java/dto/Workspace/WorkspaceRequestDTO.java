package dto.Workspace;

public class WorkspaceRequestDTO {
    private String name;
    private int capacity;
    private String description;

    public WorkspaceRequestDTO(String name, int capacity, String description) {
        this.name = name;
        this.capacity = capacity;
        this.description = description;
    }

    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public String getDescription() { return description; }
}