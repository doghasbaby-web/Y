package com.ylang.ast;

/**
 * Visitor interface for traversing AST
 */
public interface ASTVisitor<T> {
    // Existing methods
    T visitProgram(ProgramNode node);
    T visitFunctionDeclaration(FunctionDeclarationNode node);
    T visitStructDeclaration(StructDeclarationNode node);
    T visitVariableDeclaration(VariableDeclarationNode node);
    T visitIfStatement(IfStatementNode node);
    T visitForLoop(ForLoopNode node);
    T visitWhileLoop(WhileLoopNode node);
    T visitReturnStatement(ReturnStatementNode node);
    T visitBinaryExpression(BinaryExpressionNode node);
    T visitFunctionCall(FunctionCallNode node);
    T visitIdentifier(IdentifierNode node);
    T visitLiteral(LiteralNode node);
    T visitMethodDeclaration(MethodDeclarationNode node);
    T visitImplementBlock(ImplementBlockNode node);
    T visitTryCatch(TryCatchNode node);
    T visitAssignment(AssignmentNode node);

    // New methods for additional AST nodes
    T visitEnumDeclaration(EnumDeclarationNode node);
    T visitTraitDeclaration(TraitDeclarationNode node);
    T visitMatch(MatchNode node);
    T visitModule(ModuleNode node);
    T visitImport(ImportNode node);
    T visitExport(ExportNode node);
    T visitTypeAlias(TypeAliasNode node);
    T visitClosure(ClosureNode node);
    T visitClassDeclaration(ClassDeclarationNode node);
}
