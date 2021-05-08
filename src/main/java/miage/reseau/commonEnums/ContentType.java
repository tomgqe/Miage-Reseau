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
		String res = "Content-Type: ";
		switch (this) {
			case CSS:
				res+= "text/css";
				break;
			case GIF:
				res+= "image/gif";
				break;
			case HTM:
			case HTML:
				res+= "text/html";
				break;
			case ICO:
				res+= "image/gif";
				break;
			case JPG:
			case JPEG:
				res+= "image/jpeg";
				break;
			case PNG:
				res+= "image/png";
				break;
			case TXT:
				res+= "text/plain";
				break;
			case XML:
				res+= "text/xml";
				break;
			case JS:
				res+= "text/javascript";
				break;
			case TTF:
				res+= "font/ttf";
				break;
			case WOFF:
				res+= "font/woff";
				break;
			case WOFF2:
				res+= "font/woff2";
				break;
			default:
				return null;
		}
		return res;
	}
}
