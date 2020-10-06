package crt2.plugins;

import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.logical.PluginEquals;
import org.specrunner.util.xom.UtilNode;
import org.specrunner.util.xom.node.INodeHolder;
import org.specrunner.util.xom.node.INodeHolderFactory;

public class MeuPluginEquals extends PluginEquals {

    @Override
    protected boolean operation(Object obj, IContext context) throws PluginException {
        Node node = context.getNode();
        INodeHolderFactory holderFactory = SRServices.get(INodeHolderFactory.class);
        INodeHolder parent = holderFactory.newHolder(node);
        Object objExpected = null;
        Object objReceived = null;
        if (node instanceof Element) {
            if (isXml(parent)) {
                String tmp = UtilNode.getChildrenAsString((Element)parent.getNode());
                tmp = tmp.replace("&lt;", "<").replace("&gt;", ">").replace("\"", "\'");
                if (obj instanceof String) {
                    objExpected = getNormalized(String.valueOf(obj));
                    objReceived = getNormalized(String.valueOf(tmp));
                } else {
                    objExpected = obj;
                    objReceived = tmp;
                }
            } else {
                if (parent.hasAttribute(INodeHolder.ATTRIBUTE_VALUE)
                        || parent.hasAttribute(INodeHolder.ATTRIBUTE_PROPERTY)) {
                    Object tmp = parent.getObject(context, false);
                    if (obj instanceof String) {
                        objExpected = getNormalized(String.valueOf(obj));
                        objReceived = getNormalized(String.valueOf(tmp));
                    } else {
                        objExpected = obj;
                        objReceived = tmp;
                    }
                } else {
                    objExpected = holderFactory.newHolder(UtilNode.getLeft(node)).getObject(context, true);
                    objReceived = holderFactory.newHolder(UtilNode.getRight(node)).getObject(context, true);
                }
            }
        }
        try {
            return verify(context, parent.getComparator(), objExpected, objReceived);
        } catch (SpecRunnerException e) {
            throw new PluginException(e);
        }
    }

    private boolean isXml(INodeHolder parent) {
        return parent.attributeContains("reader","xml");
    }
    
    /**
     * Default behavior.
     * 
     * @param context
     *            The context.
     * @param node
     *            The node.
     * @return The value object.
     * @throws PluginException
     *             On evaluation errors.
     */
    protected Object getObjectValue(IContext context, Node node) throws PluginException {
        INodeHolder holder = SRServices.get(INodeHolderFactory.class).newHolder(node);
        if(isXml(holder)){
            return holder.hasAttribute(INodeHolder.ATTRIBUTE_VALUE) ? getValue() : UtilNode.getChildrenAsString((Element)holder.getNode()).replace("&lt;", "<").replace("&gt;", ">").replace("\"", "\'");
        }
        return isEval() ? holder.getObject(context, true) : node.getValue();
    }
}
