import org.json.{JSONObject, JSONArray}
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.ScriptableObject._
import org.mozilla.javascript._
import scala.collection.JavaConversions._


class ScriptableObjectConverter {

	def scriptableToJsonObject(scriptableObject:ScriptableObject): JSONObject = {
		val jsonObject = new JSONObject
		scriptableObject.getAllIds.foldLeft(jsonObject) {(json, key)=>
			ScriptableObject.getProperty(scriptableObject, key.toString) match {
				case value:ScriptableObject => {
					json.put(key.toString, scriptableToJsonObject(value))
				}
				case value => {
					json.put(key.toString, value)
				}
			}
		}
	}
}