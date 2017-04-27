package spec

import com.outr.arango._
import com.outr.arango.managed._
import com.outr.arango.DocumentOption
import org.scalatest.{AsyncWordSpec, Matchers}

class ManagedSpec extends AsyncWordSpec with Matchers {
  "Managed Graph" should {
    "create the graph" in {
      ExampleGraph.init().map { b =>
        b should be(true)
      }
    }
    "create the Fruit collection" in {
      ExampleGraph.fruit.create().map { response =>
        response.error should be(false)
      }
    }
    "insert Apple" in {
      ExampleGraph.fruit.insert(Fruit("Apple", Some("Apple"))).map { f =>
        f._id should be(Some("fruit/Apple"))
        f._key should be(Some("Apple"))
        f._rev shouldNot be(None)
        f.name should be("Apple")
      }
    }
    "insert Banana" in {
      ExampleGraph.fruit.insert(Fruit("Banana")).map { f =>
        f._id shouldNot be(None)
        f._key shouldNot be(None)
        f._rev shouldNot be(None)
        f.name should be("Banana")
      }
    }
    "insert Cherry" in {
      ExampleGraph.fruit.insert(Fruit("Cherry")).map { f =>
        f._id shouldNot be(None)
        f._key shouldNot be(None)
        f._rev shouldNot be(None)
        f.name should be("Cherry")
      }
    }
    "query Apple back by key" in {
      ExampleGraph.fruit.byKey("Apple").map { f =>
        f.name should be("Apple")
      }
    }
    "query all fruit back" in {
      val query = aql"FOR f IN fruit RETURN f"
      ExampleGraph.fruit.cursor(query).map { response =>
        response.error should be(false)
        response.count should be(Some(3))
        response.result.map(_.name).toSet should be(Set("Apple", "Banana", "Cherry"))
      }
    }
    "delete the graph" in {
      ExampleGraph.delete().map { b =>
        b should be(true)
      }
    }
  }

  object ExampleGraph extends Graph("example") {
    val fruit: Collection[Fruit] = collection[Fruit]("fruit")
    val content: PolymorphicCollection[Content] = polymorphic3[Content, Image, Video, Audio]("content")
  }

  case class Fruit(name: String,
                   _key: Option[String] = None,
                   _id: Option[String] = None,
                   _rev: Option[String] = None) extends DocumentOption

  trait Content extends PolymorphicDocumentOption {
    def name: String
  }

  case class Image(name: String,
                   width: Int,
                   height: Int,
                   _key: Option[String] = None,
                   _id: Option[String] = None,
                   _rev: Option[String] = None,
                   _type: String = "image") extends Content

  case class Video(name: String,
                   width: Int,
                   height: Int,
                   length: Double,
                   _key: Option[String] = None,
                   _id: Option[String] = None,
                   _rev: Option[String] = None,
                   _type: String = "video") extends Content

  case class Audio(name: String,
                   length: Double,
                   _key: Option[String] = None,
                   _id: Option[String] = None,
                   _rev: Option[String] = None,
                   _type: String = "audio") extends Content
}