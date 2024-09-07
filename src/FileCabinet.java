import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

interface Cabinet {
    // zwraca dowolny element o podanej nazwie
    Optional<Folder>
    findFolderByName(String name);

    // zwraca wszystkie foldery podanego rozmiaru SMALL/MEDIUM/LARGE
    List<Folder> findFoldersBySize(String size);

    //zwraca liczbę wszystkich obiektów tworzących strukturę
    int count();
}

public class FileCabinet implements Cabinet {
    private List<Folder> folders;
    private int count;

    public FileCabinet(List<Folder> folders) {
        this.folders = folders;
    }

    @Override
    public Optional<Folder> findFolderByName(String name) {
        return findFolderByNameRecursive(name, this.folders);
    }

    private Optional<Folder> findFolderByNameRecursive(String name, List<Folder> folders){
        Optional<Folder> result;
        //szukamy folder wsrod wszystkich folderow na liscie po nazwie
        for(Folder folder : folders){
            if(folder.getName().equals(name)){
                return Optional.of(folder);
            }
            //jesli folder zawiera inne foldery, to szukamy tez rekursywnie wsrod nich i td
            if(folder instanceof MultiFolder){
                result = findFolderByNameRecursive(name, ((MultiFolder) folder).getFolders());
                return result;
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Folder> findFoldersBySize(String size) {
        return findFoldersBySizeRecursive(size, this.folders);
    }

    private List<Folder> findFoldersBySizeRecursive(String size, List<Folder> folders){
        List<Folder> result = new ArrayList<>();
        for(Folder folder : folders){
            if(folder.getSize().equals(size)){
                result.add(folder);
            }
            if(folder instanceof MultiFolder){
                result.addAll(findFoldersBySizeRecursive(size, ((MultiFolder) folder).getFolders()));
            }
        }
        return result;
    }

    @Override
    public int count() {
        return countAll(this.folders);
    }
    private int countAll(List<Folder> folders){
        int numberOfFolders = folders.size();
        for(Folder folder : folders){
            if(folder instanceof MultiFolder){
                numberOfFolders += countAll(((MultiFolder) folder).getFolders());
            }
        }
        return numberOfFolders;
    }
}

interface Folder {
    String getName();
    String getSize();
}

interface MultiFolder extends Folder {
    List<Folder> getFolders();
}

class FolderClass implements MultiFolder {
    private File folder;

    public FolderClass(File folder){
        this.folder = folder;
    }
    public String getName() {
        return folder.getName();
    }
    public String getSize(){
        return calc(this.folder);
    }

    //wyliczam rozmiar każdego pliku w folderze i zsumowac
    private String calc(File folder){
        String folderSize;
        long sizeInBytes = 0;
        long sizeInKBytes = 0;
        long sizeInMBytes = 0;
        File[] allFiles = folder.listFiles();
        if(allFiles != null){
            for(File file : allFiles){
                if(file.isFile()){
                    sizeInBytes += file.length();
                    sizeInKBytes = sizeInBytes / 1024;
                    sizeInMBytes = sizeInKBytes / 1024;
                } else if(file.isDirectory()) { //podfolder
                    sizeInBytes += Long.parseLong(new FolderClass(file).getSize());
                    sizeInKBytes = sizeInBytes / 1024;
                    sizeInMBytes = sizeInKBytes / 1024;
                }
            }
        }
        if(sizeInMBytes < 500){
            folderSize = "SMALL";
        }
        if (sizeInMBytes >= 500 && sizeInMBytes <= 3000) {
            folderSize = "MIDDLE";
        } else {
            folderSize = "LARGE";
        }
        return folderSize;
    }
    public List<Folder> getFolders(){
        return null;
    }
}