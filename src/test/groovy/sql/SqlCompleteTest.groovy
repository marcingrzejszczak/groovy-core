package groovy.sql

import org.axiondb.jdbc.AxionDataSource

class SqlCompleteTest extends GroovyTestCase {

    void testSqlQuery() {
        sql = createSql()     
        
        results = [:]
        sql.queryEach("select * from PERSON") { results.put(it.firstname, it.lastname) }
        
        expected = ["James":"Strachan", "Bob":"Mcwhirter", "Sam":"Pullara"]
					
        assert results == expected
    }
    
    void testSqlQueryWithWhereClause() {
        sql = createSql()     
        
        foo = "drink"
        results = []
        sql.queryEach("select * from FOOD where type=${foo}") { results.add(it.name) }
        
        expected = ["beer", "coffee"]
        assert results == expected
    }
    
    void testSqlQueryWithWhereClauseWith2Arguments() {
        sql = createSql()     
        
        foo = "cheese"
        bar = "edam"
        results = []
        sql.queryEach("select * from FOOD where type=${foo} and name != ${bar}") { results.add(it.name) }
        
        expected = ["brie", "cheddar"]
        assert results == expected
    }

    void testSqlQueryWith2ParametersUsingQuestionMarkNotation() {
        sql = createSql()     
        
        results = []
        sql.queryEach("select * from FOOD where type=? and name != ?", ["cheese", "edam"]) { results.add(it.name) }
        
        expected = ["brie", "cheddar"]
        assert results == expected
    }

    void testDataSet() {
        sql = createSql()     
        
        results = []
        people = sql.dataSet("PERSON")
        people.each { results.add(it.firstname) }
        
        expected = ["James", "Bob", "Sam"]
        assert results == expected
    }
    
    void testDataSetWithClosurePredicate() {
        sql = createSql()     
        
        results = []
        food = sql.dataSet("FOOD")
        food.findAll { it.type == "cheese" }.each { results.add(it.name) }
        
        expected = ["edam", "brie", "cheddar"]
        assert results == expected
    }
    
    void testUpdatingDataSet() {
        sql = createSql()     
        
        results = []
        features = sql.dataSet("FEATURE")
        features.each { 
            /** @todo Axion doesn't yet support ResultSet updating
            if (it.id == 1) {
                it.name = it.name + " Rocks!"
                println("Changing name to ${it.name}")
            }
            */
            results.add(it.name) 
        }
        
        expected = ["GDO", "GPath", "GroovyMarkup"]
        assert results == expected
    }
    
    protected createSql() {
        sql = newSql("jdbc:axiondb:foo" + getName())
        
        sql.execute("create table PERSON ( firstname varchar, lastname varchar )")     
        sql.execute("create table FOOD ( type varchar, name varchar)")
        sql.execute("create table FEATURE ( id integer, name varchar)")
        
        // now lets populate the datasets
        people = sql.dataSet("PERSON")
        people.add( firstname:"James", lastname:"Strachan" )
        people.add( firstname:"Bob", lastname:"Mcwhirter" )
        people.add( firstname:"Sam", lastname:"Pullara" )
        
        food = sql.dataSet("FOOD")
        food.add( type:"cheese", name:"edam" )
        food.add( type:"cheese", name:"brie" )
        food.add( type:"cheese", name:"cheddar" )
        food.add( type:"drink", name:"beer" )
        food.add( type:"drink", name:"coffee" )
        
        features = sql.dataSet("FEATURE")
        features.add( id:1, name:'GDO' )
        features.add( id:2, name:'GPath' )
        features.add( id:3, name:'GroovyMarkup' )
        return sql
    }
    
    protected newSql(String uri) {
	    dataSource = new AxionDataSource("jdbc:axiondb:foo" + getName())
	    return new Sql(dataSource)
    }
}
