package utils_soft.NpcEditor;

import l2open.database.Filter;
import l2open.database.Resource;
import l2open.database.ResourceProvider;
import l2open.database.schemes.builders.DroplistBuilder;
import l2open.database.schemes.builders.NpcBuilder;
import l2open.database.schemes.builders.NpcElementBuilder;
import l2open.database.schemes.builders.NpcskillsBuilder;
import l2open.database.schemes.resources.DroplistResource;
import l2open.database.schemes.resources.NpcElementResource;
import l2open.database.schemes.resources.NpcResource;
import l2open.database.schemes.resources.NpcskillsResource;

import java.util.List;

public class NpcEditorResource {

    private static NpcEditorResource resource;

    private final Resource<NpcResource, NpcBuilder> npcResource;
    private final Resource<NpcElementResource, NpcElementBuilder> npcElementResource;
    private final Resource<DroplistResource, DroplistBuilder> dropListResource;
    private final Resource<NpcskillsResource, NpcskillsBuilder> npcSkillsResource;

    private NpcEditorResource() {
        this.npcResource = new ResourceProvider<>(NpcResource.class);
        this.npcElementResource = new ResourceProvider<>(NpcElementResource.class);
        this.dropListResource = new ResourceProvider<>(DroplistResource.class);
        this.npcSkillsResource = new ResourceProvider<>(NpcskillsResource.class);
    }

    public static NpcEditorResource getInstance() {
        if (resource == null){
            resource = new NpcEditorResource();
        }
        return resource;
    }

    public Resource<NpcResource, NpcBuilder> getNpcResource() {
        return npcResource;
    }

    public Resource<NpcElementResource, NpcElementBuilder> getNpcElementResource() {
        return npcElementResource;
    }

    public Resource<DroplistResource, DroplistBuilder> getDropListResource() {
        return dropListResource;
    }

    public Resource<NpcskillsResource, NpcskillsBuilder> getNpcSkillsResource() {
        return npcSkillsResource;
    }

    public List<NpcResource> getNpcFilteredList(String filter, String filterValue,int limit, int offset){
        switch (filter) {
            case "npcname": return getNpcResource().findList(new Filter().LIKE(NpcResource.NAME, filterValue).ORDER_BY(NpcResource.ID).LIMIT(limit).OFFSET(offset));
            case "npcid": return getNpcResource().findList(new Filter().LIKE(NpcResource.ID, filterValue).ORDER_BY(NpcResource.ID).LIMIT(limit).OFFSET(offset));
            case "npctype": return getNpcResource().findList(new Filter().LIKE(NpcResource.TYPE, filterValue).ORDER_BY(NpcResource.ORDINAL).LIMIT(limit).OFFSET(offset));
            default: return getNpcResource().findList(new Filter().ORDER_BY(NpcResource.ID).LIMIT(limit).OFFSET(offset));
        }
    }

    public NpcResource getNpcById(int npcId){
        return NpcEditorResource.getInstance().getNpcResource().find(new Filter().WHERE(NpcResource.ID, npcId));
    }

}
