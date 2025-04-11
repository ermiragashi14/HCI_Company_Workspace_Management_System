package dto.Workspace;

public class WorkspaceResponseDTO {
    private int id;
    private String name;
    private int capacity;
    private String description;

    public WorkspaceResponseDTO(int id, String name, int capacity, String description) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.description = description;
    }


    public int getId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public String getDescription() { return description; }


    public void setName(String name) { this.name = name; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setDescription(String description) { this.description = description; }
}
