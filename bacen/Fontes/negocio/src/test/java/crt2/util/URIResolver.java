package crt2.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.specrunner.plugins.core.include.IResolver;

public class URIResolver implements IResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(URIResolver.class);

    @Override
    public URI resolve(URI base, URI relative) {
        URI uri = base.resolve(relative);
        if (!new File(uri).exists()) {
            String strBase = base.toString();
            String strRelative = relative.toString();
            String regex = "/";
            String[] splitBase = strBase.split(regex);
            String[] splitRelative = strRelative.split(regex);
            int count = 0;
            while (count < splitRelative.length && "..".equals(splitRelative[count])) {
                count++;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < splitBase.length - count; i++) {
                sb.append(splitBase[i] + regex);
            }
            for (int i = count; i < splitRelative.length; i++) {
                sb.append(splitRelative[i] + regex);
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            try {
                uri = new URI(sb.toString());
            } catch (URISyntaxException e) {
                LOGGER.debug(e.getMessage(), e);
            }
            }
        }
        return uri;
    }

}
