package miage.reseau.commonEnums;

import java.io.OutputStream;


//Enum des extensions gérées par le serveur
// le toString fournit directement le header Content-Type qui correspond
public enum ContentType {
	CSS("CSS"),
	GIF("GIF"),
	HTM("HTM"),
	HTML("HTML"),
	ICO("ICO"),
	JPG("JPG"),
	JPEG("JPEG"),
	PNG("PNG"),
	TXT("TXT"),
	XML("XML"),
    JS("JS"),
    TTF("TTF"),
    WOFF("WOFF"),
    WOFF2("WOFF2");

	private final String extension;

	ContentType(String extension) {
		this.extension = extension;
	}

	@Override
	public String toString() {
		switch (this) {
			case CSS:
				return "Content-Type: text/css";
			case GIF:
				return "Content-Type: image/gif";
			case HTM:
			case HTML:
				return "Content-Type: text/html";
			case ICO:
				return "Content-Type: image/gif";
			case JPG:
			case JPEG:
				return "Content-Type: image/jpeg";
			case PNG:
				return "Content-Type: image/png";
			case TXT:
				return "Content-type: text/plain";
			case XML:
				return "Content-type: text/xml";
			case JS:
				return "text/javascript";
			case TTF:
				return "font/ttf";
			case WOFF:
				return "font/woff";
			case WOFF2:
				return "font/woff2";
			default:
				return null;
		}
	}
}
